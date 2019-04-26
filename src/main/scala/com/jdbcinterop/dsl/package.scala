package com.jdbcinterop

import com.jdbcinterop.core.{Abort, ConnWrapper, OverLongList, PSWrapper}

import scala.reflect.runtime.universe._

package object dsl {
  def openList[T](tuples: T*): SQLList[T] = new SQLList(tuples, abortIfEmpty = false, open = true)
  def array[A : TypeTag](elems: A*): SQLArray[A] = SQLArray(elems)
  def UNSAFE_literal(text: String): UNSAFE_Direct = UNSAFE_Direct(text)
  def UNSAFE_relation(name: String): UNSAFE_Direct = {
    ???
  }
  def setter(op: (PSWrapper, Int) => Unit): Setter = Setter(op)

  implicit class SQLHelper(val sc: StringContext) extends AnyVal {
    def sql2[A: TypeTag, B: TypeTag](a: A, b: B): String = {
      typeOf[A].toString + " " + typeOf[B].toString
    }

    def sql2[A : TypeTag](a: A): String = typeOf[A].toString

    def sql(args: Any*)(ctx: ConnWrapper): Seq[PSWrapper] = {
      val strings = sc.parts.iterator
      val expressions = args.iterator
      var buf = new StringBuffer(strings.next)
      var values = scala.collection.mutable.ListBuffer.empty[Any]
      def putValue(value: Any): String = {
        values += value
        "?"
      }

      def doExpr[E : TypeTag](depth: Int)(expr: E): String = {
        def doList[A](list: SQLList[A]): String = {
          if (list.values.isEmpty && list.abortIfEmpty) throw new Abort()
          list.maxLength.foreach(maxLen=>{
            if (list.values.length > maxLen) throw new OverLongList(list.values.length, maxLen)
          })

          val str = list.values.map(doExpr[Any](depth + 1)).mkString(",")
          if (list.open) "\n" + str + "\n"
          else "(" + str + ")"
        }

        expr match {
          case l: SQLList[_] => doList(l)
          case l: UNSAFE_Direct => l.a.toString
          case s: Setter => putValue(s)  // ConnWrapper#autoSet discriminator expects Setters
          case p: Product if expr.getClass.toString.startsWith("class scala.Tuple") =>
            doList(SQLList(p.productIterator.toSeq, abortIfEmpty = false))
          case _ => putValue(expr)
        }
      }

      try {
        while(strings.hasNext) {
          buf append doExpr(1)(expressions.next)
          buf append strings.next
        }

        val psw = ctx.prepareStatement(buf.toString.trim)
        for (i <- values.indices) psw.autoSet(i + 1, values(i))
        Seq(psw)
      } catch {
        case a: Abort => Nil
      }

    }
  }
}
