package com.jdbcinterop.core

import scala.reflect.runtime.universe._
import scala.collection.mutable.ListBuffer

object QueryBuilder {
  def empty: QueryBuilder = new QueryBuilder
}

class QueryBuilder {
  //private var query: Query = Query.empty
  private val root = ListBuffer.empty[QueryNode]
  private val vars = ListBuffer.empty[Any]

  private def valueExpr[A : TypeTag](sample: A, isList: Boolean = false): QueryNode = {
    valueExpr(typeOf[A])
  }

  private def valueExpr(tpe: Type): ParamNode = {
    val setter: Setters.PSSetter[_]
      = Setters.find(tpe).getOrElse(throw new IllegalArgumentException("Unsupported type: " + tpe.toString))
      ParamNode(setter, tpe)
  }

  private def optionExpr(tpe: Type): OptionNode = {
    val tpeArg = tpe.typeArgs.headOption.getOrElse(throw new IllegalArgumentException("Use Option[T](null) for None"))
    OptionNode(valueExpr(tpeArg), tpeArg)
  }

  private def listExpr(tpe: Type): ListNode = {
    // TODO: Screen out empty list
    val targ = tpe.typeArgs.headOption.getOrElse(throw new IllegalArgumentException("No type arg for list"))
   ListNode(mkNode(targ))
  }

  private def tupleExpr(tpe: Type): TupleNode = {
    val targs = tpe.typeArgs
    TupleNode(targs.map(mkNode))
  }

  private def mkNode(tpe: Type): VarNode = {
    tpe.typeSymbol.toString match {
      case "class SQLList" => listExpr(tpe)
      case "class UNSAFE_Direct" => DirectNode() // needs value at runtime unlike literal node
      case "class Setter" => valueExpr(tpe)
      case "class Option" | "class Some" | "object None" => optionExpr(tpe)
      case s: String if s.startsWith("class Tuple") => tupleExpr(tpe)
      case _ => valueExpr(tpe)
    }
  }

  def addVariable[A : TypeTag](expr: A): QueryBuilder = expr.getClass.toString match {
    case "class scala.runtime.BoxedUnit" => this // calling interpolation with 0 arguments actually passes Unit
    case _ =>
      vars += expr
      val tpe = typeOf[A]
      root += mkNode(tpe)
      this
  }

  def addLiteral(text: String): QueryBuilder = {
    root += LiteralNode(text)
    this
  }

  private def query(): Seq[Query] = {
    val repeats: List[List[Any]] = vars.map {
      case l: SQLList[_] => l.chunks
      case a: Any => List(a)
    }.toList

    // append `1` in case repeats is empty (`max` on empty Seq throws)
    val repCount: Int = (repeats.map(_.length) :+ 1).max
    (0 until repCount).map(i=>repeats.map(l=>l.lift(i).getOrElse(l.head))).map(repeat=>{
      val valIter = repeat.toIterator
      root.foldLeft(Query.empty)((b, a)=> a match {
        case l: LiteralNode => b.appendLiteral(l.text)
        case v: VarNode => v.appendTo(b)(valIter.next())
      })
    })
  }

  // Inserts variables directly into SQL and calls `toString`.
  // Intended for debugging syntax errors in SQL generation.
  def printQuery(): Unit = {
    val q = query().head
    val varIter = q.setters.map(_.__value).toIterator
    println(q.sqlFrags.map {
        case "?" => varIter.next() match {
          case s: String => s""""$s""""
          case a: Any => a.toString
        }
        case s: String => s
      }.mkString(""))
  }

  def mkStatement(conn: ConnWrapper): Seq[PSWrapper] ={
    query().map(_.prepare().exec(conn))
  }
}


object Query {
  def empty:  Query = Query(Nil, 0, Nil)
}

case class Query(sqlFrags: List[String], qmCount: Int, setters: List[SetValue]) {
  def unsafeAppend(sqlFrags: List[String] = Nil, qmCount: Int = 0, setters: List[SetValue] = Nil): Query = {
    this.copy(
      sqlFrags = this.sqlFrags ++ sqlFrags,
      qmCount = this.qmCount + qmCount,
      setters = this.setters ++ setters
    )
  }

  def appendSetter(setter: SetValue): Query = appendQM.unsafeAppend(setters = List(setter))

  def incQMCount(amount: Int = 1): Query = unsafeAppend(qmCount = amount)

  def appendLiteral(text: String): Query = {
    unsafeAppend(
      sqlFrags = List(text),
      qmCount = if (text == "?") 1 else 0)
  }

  def appendParen(open: Boolean): Query = appendLiteral(if (open) "(" else ")")

  def withParen(forReal: Boolean)(op: Query => Query): Query = {
    if (forReal) op(this.appendParen(open = true)).appendParen(open = false) else op(this)
  }

  def appendComma: Query = appendLiteral(",")

  def trimEnd(text: String): Query = {
    if (sqlFrags.lastOption.orNull == text) copy(sqlFrags = sqlFrags.slice(0, sqlFrags.length - 1))
    else this
  }

  private def appendQM: Query = {
    (if (sqlFrags.lastOption.getOrElse("") == "?") this.appendLiteral(",") else this).appendLiteral("?")
  }

  def prepare(): QueryReady = {
    val sql = sqlFrags.mkString("")
    //println("[QueryReady]" + sql)
    QueryReady(
      sql = sql,
      setters = setters
    )
  }
}


case class QueryReady(sql: String, setters: Seq[SetValue]) {
  def exec(conn: ConnWrapper): PSWrapper = {
    val psw = conn.prepareStatement(sql)
    setters.foreach(_.set(psw))
    psw
  }
}


trait SetValue {
  // for debugging reference @DB
  val __value: Any
  def set(psw: PSWrapper): Unit
}


trait QueryNode

trait VarNode extends QueryNode {
  def appendTo(query: Query)(value: Any): Query
}

case class ParamNode(setValue: Setters.PSSetter[_], tpe: Type) extends VarNode {
  override def appendTo(query: Query)(value: Any): Query = {
    val setter = new SetValue {
      val __value: Any = value
      override def set(psw: PSWrapper): Unit = setValue.set(SetValueCtx(
        psw = psw,
        idx = query.qmCount + 1,
        flavor = psw.flavor,
        value = value
      ))
    }
    query.appendSetter(setter)
  }
}

case class OptionNode(paramNode: ParamNode, tpe: Type) extends VarNode {
  override def appendTo(query: Query)(value: Any): Query = {
    value.asInstanceOf[Option[_]].map(v=>paramNode.appendTo(query)(v)).getOrElse({
      val setter = new SetValue {
        override val __value: Any = value
        override def set(psw: PSWrapper): Unit = psw.setNull(query.qmCount + 1)
      }
      query.appendSetter(setter)
    })
  }
}

object DirectNode {
  def apply(): DirectNode = new DirectNode()
}

class DirectNode extends VarNode {
  override def appendTo(query: Query)(value: Any): Query = {
    query.appendLiteral(value.asInstanceOf[UNSAFE_Direct].a.toString)
  }
}

case class LiteralNode(text: String) extends QueryNode

// holds param nodes or tuple nodes
 case class TupleNode(children: List[QueryNode]) extends VarNode {
  def appendTo(query0: Query)(value: Any): Query = query0.withParen(forReal = true)(query0=>{
    var query: Query = query0
    val values = value.asInstanceOf[Product].productIterator.toList
    for (i <- children.indices) {
      val value = values(i)
      var node = children(i)
      query = node match {
        case l: LiteralNode => query.appendLiteral(l.text)
        case n: VarNode => n.appendTo(query)(value  )
      }
    }
    query
  })
}

case class ListNode(child: VarNode) extends VarNode {
  def appendTo(query0: Query)(value: Any): Query = {
    val list = value.asInstanceOf[SQLList[_]]
    query0.withParen(forReal = !list.open)(query0=>{
      var query: Query = query0
      for (value <- list.values) query = child.appendTo(query)(value).appendComma
      query.trimEnd(",")
    })
  }
}


//case class BranchNode(tpe: ExprTypes.ExprType, children: List[QueryNode], isList: Boolean) extends QueryNode
case class BranchNode(children: List[QueryNode]) extends QueryNode


