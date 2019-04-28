package com.jdbcinterop.core

import java.sql.{Connection, PreparedStatement, ResultSet, Types}

import org.postgresql.util.PGobject
import play.api.libs.json.{JsValue, Json}
import com.jdbcinterop.core.DBFlavor._

import scala.util.Try


trait PSWrapper {
  val preparedStatement: PreparedStatement
  val flavor: DBFlavorTrait

  private def execAndClose[R](op: PreparedStatement => R) = {
    val res = op(preparedStatement)
    preparedStatement.close()
    res
  }

  def execute(): Boolean = execAndClose(_.execute())

  def executeUpdate(): Int = execAndClose(_.executeUpdate())

  def executeQuery(): RSWrapper = new RSWrapper {
    override val resultSet: ResultSet = preparedStatement.executeQuery() // we must not close as caller needs to loop over results set
  }

  def mkPGJson(json: String): PGobject = {
    var po = new PGobject()
    po.setType("json")
    po.setValue(json)
    po
  }

  def setJson(idx: Int, json: JsValue): Unit = {
    flavor match {
      case PostgreSQL => preparedStatement.setObject(idx, mkPGJson(json.toString()))
      case _ => throw new IllegalArgumentException("No JSON support for " + flavor)
    }
  }

  def mkArray[A](array: SQLArray[A]): java.sql.Array = {
    preparedStatement.getConnection.createArrayOf(
      TypeMap(array.typeArg, flavor).nativeType,
      array.values.map(_.asInstanceOf[AnyRef]).toArray)
  }

  def setArray[A](idx: Int, array: SQLArray[A]): Unit = {
   preparedStatement.setArray(idx, mkArray(array))
  }

  def setSetter(idx: Int, setter: Setter): Unit = setter.op(this, idx)

  def setNull(idx: Int): Unit = preparedStatement.setNull(idx, Types.NULL)
}

trait RSWrapper {
  val resultSet: ResultSet
  def getInt(columnLabel: String): Int = resultSet.getInt(columnLabel)
  def getInt(columnIndex: Int): Int = resultSet.getInt(columnIndex)
  def getLong(columnLabel: String): Long = resultSet.getLong(columnLabel)
  def getLong(columnIndex: Int): Long = resultSet.getLong(columnIndex)
  def getDouble(columnLabel: String): Double = resultSet.getDouble(columnLabel)
  def getDouble(columnIndex: Int): Double = resultSet.getDouble(columnIndex)
  def getString(columnLabel: String): Option[String] = Option(resultSet.getString(columnLabel))
  def getString(columnIndex: Int): Option[String] = Option(resultSet.getString(columnIndex))
  def getArray[A](columnLabel: String): Array[A] = resultSet.getArray(columnLabel).getArray.asInstanceOf[Array[A]]
  def getArray[A](columnIndex: Int): Array[A] = resultSet.getArray(columnIndex).getArray.asInstanceOf[Array[A]]
  def getJsValue(columnLabel: String): JsValue = Json.parse(resultSet.getString(columnLabel))
  def getJsValue(columnIndex: Int): JsValue = Json.parse(resultSet.getString(columnIndex))
}


trait ConnWrapper {
  val conn: Connection
  val flavor: DBFlavorTrait

  def prepareStatement(sql: String): PSWrapper = {
    val self = this
    new PSWrapper {override val preparedStatement: PreparedStatement = conn.prepareStatement(sql)
      override val flavor: DBFlavorTrait = self.flavor
    }
  }
}


trait ConnectionProvider {
  val flavor: DBFlavorTrait
  def withConnection[R](op: Connection => R): R
}


trait ExecScope extends ConnectionProvider {
  protected val depth: Int

  def withSession[R](op: (Session) => R): R = withConnection(conn=>{
    val self = this
    val res = op(new Session {
      override val depth: Int = self.depth + 1
      override val flavor: DBFlavorTrait = self.flavor
      override def withConnection[R1](op: (Connection) => R1): R1 = {
        val res = op(conn)
        conn.commit()
        res
      }
    })

    res
  })

  def withTransaction[R](op: Transaction => R): R = withConnection(conn=>{
    val self = this
    val res = op(new Transaction {
      override val depth: Int = self.depth + 1
      override val flavor: DBFlavorTrait = self.flavor
      override def withConnection[R1](op: (Connection) => R1): R1 = op(conn)
    })

    conn.commit()
    res
  })

  protected def execAux[R](op: ConnWrapper => R): R = {
    withConnection[R](conn0 =>{
      val self = this
      val cw = new ConnWrapper {
        override val conn: Connection = conn0
        override val flavor: DBFlavorTrait = self.flavor
      }
      cw.conn.setAutoCommit(false)

      var res: Option[R] = None
      try {
        res = Some(op(cw))
        if (depth == 0) {
          cw.conn.commit()
          cw.conn.close()
        }
      } catch {
        case t: Throwable => Try(cw.conn.close()); throw t
      }

      res.get
    })
  }

  def raw(sql: String): Boolean = {
    exec(QueryBuilder.empty.addLiteral(sql).mkStatement)
  }

  def exec(op: MkStatement): Boolean = execAux(c=>op(c).map(_.execute())).headOption.getOrElse(false)

  def execUpdate(update: MkStatement): Long = execUpdates(Seq(update))

  def execUpdates(updates: Iterable[MkStatement]): Long = {
    execAux[Long](conn=>{
      updates.flatMap(u=>u(conn).map(_.executeUpdate())).sum
    })
  }

  def execQuery[R](query: MkStatement)(each: RSWrapper => R): Seq[R] = {
    execAux[Seq[R]](conn=>{
      query(conn).flatMap(psw=>{
        val rsw = psw.executeQuery()
        val res = scala.collection.mutable.ListBuffer.empty[R]
        while (rsw.resultSet.next()) {
          res += each(rsw)
        }
        psw.preparedStatement.close()
        res
      })
    })
  }
}


trait Session extends ExecScope {
  override def withSession[R](op: (Session) => R): R
    = throw new IllegalAccessError("Recursive session scopes not allowed")
}


trait Transaction extends ExecScope {
  override def withTransaction[R](op: (Transaction) => R): R
    = throw new IllegalAccessError("Recursive transaction scopes not allowed")

  override def withSession[R](op: (Session) => R): R
    = throw new IllegalAccessError("No transactional session cannot be created from transaction scope")
}


object DB {}

/* Notes On PreparedStatement and Connection Management:
* - Close prepared statements once no longer needed
*   + PreparedStatement must be closed before a new one created
* - Close connections:
*   + after session callback completes
*   + after `execAux` callback completes when at depth 0
*
* - PSWrapper closes automatically for all but `executeQuery`. For `executeQuery` prepared statement
*   must stay open until caller done with ResultSet.
*
* */
trait DB extends ExecScope {
  override val depth: Int = 0
}
