package com.jdbcinterop.core

import java.sql.{Connection, PreparedStatement, ResultSet, Types}
import org.postgresql.util.PGobject
import play.api.libs.json.{JsValue, Json}

import com.jdbcinterop.core.DBFlavor._


trait PSWrapper {
  val preparedStatement: PreparedStatement
  val flavor: DBFlavorTrait

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
  protected val isTransaction: Boolean
  protected val isSession: Boolean

  def withSession[R](op: (Session) => R): R = withConnection(conn=>{
    val self = this
    op(new Session {
      override val flavor: DBFlavorTrait = self.flavor
      override def withConnection[R1](op: (Connection) => R1): R1 = op(conn)
    })
  })

  def withTransaction[R](op: Transaction => R): R = withConnection(conn=>{
    val self = this
    op(new Transaction {
      override val flavor: DBFlavorTrait = self.flavor
      override def withConnection[R1](op: (Connection) => R1): R1 = op(conn)
    })
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
        if (!isTransaction) cw.conn.commit()
      } finally {
        if (!isTransaction && !isSession) cw.conn.close()
      }

      res.get
    })
  }

  def raw(sql: String): Boolean = {
    execAux(conn=>{
      conn.conn.prepareStatement(sql).execute()
    })
  }

  def exec(op: MkStatement): Boolean = execAux(c=>op(c).map(_.preparedStatement.execute())).headOption.getOrElse(false)

  def execUpdate(update: MkStatement): Long = execUpdates(Seq(update))

  def execUpdates(updates: Iterable[MkStatement]): Long = {
    execAux[Long](conn=>{
      updates.flatMap(u=>u(conn).map(_.preparedStatement.executeUpdate())).sum
    })
  }

  def execQuery[T](query: MkStatement)(each: RSWrapper => T): Iterable[T] = {
    execAux[Iterable[T]](conn=>{
      query(conn).flatMap(psw=>{
        val rs = psw.preparedStatement.executeQuery()
        val res = scala.collection.mutable.ListBuffer.empty[T]
        val rsw = new RSWrapper {
          override val resultSet: ResultSet = rs
        }
        while (rs.next()) {
          res += each(rsw)
        }
        res
      })
    })
  }
}


trait Session extends ExecScope {
  protected val isTransaction: Boolean = false
  protected val isSession: Boolean = true

  override def withSession[R](op: (Session) => R): R
    = throw new IllegalAccessError("Recursive session scopes not allowed")
}


trait Transaction extends ExecScope {
  protected val isTransaction: Boolean = true
  protected val isSession: Boolean = true

  override def withTransaction[R](op: (Transaction) => R): R
    = throw new IllegalAccessError("Recursive transaction scopes not allowed")

  override def withSession[R](op: (Session) => R): R
    = throw new IllegalAccessError("No transactional session cannot be created from transaction scope")
}


object DB {}

trait DB extends ExecScope {
  protected val isTransaction: Boolean = false
  protected val isSession: Boolean = false
}
