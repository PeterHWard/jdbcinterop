package com.jdbcinterop

import scala.reflect.runtime.universe._
import java.sql.Types

import com.jdbcinterop.core.DBFlavor._

package object core {
  type MkStatement = ConnWrapper => Seq[PSWrapper]

  trait DBFlavorTrait

  case object DBFlavor {

    case object Oracle extends DBFlavorTrait

    case object PostgreSQL extends DBFlavorTrait

    case object H2 extends  DBFlavorTrait
  }

  object SQLType {
    protected val pgTypes = Seq(
      SQLType("text", Types.CLOB, Seq(typeOf[String], typeOf[java.lang.String]))
    )
    /* protected val toPGTypes: Map[String, SQLType] = Map(
       typeOf[String].toString  -> ,
       typeOf[Long].toString -> SQLType("bigint", Types.BIGINT),
       typeOf[Int].toString -> SQLType("int", Types.NUMERIC),
       typeOf[Double].toString -> SQLType("numeric", Types.NUMERIC)
     )

     protected val toOracleTypes = Map(
       typeOf[String] -> SQLType("clob", Types.CLOB)
     )

     protected val toH2Types = Map(
       typeOf[String] -> SQLType("text", Types.CLOB)
     )*/

    def apply(tpe: Type, flavor: DBFlavorTrait): SQLType = {
      (flavor match {
        case PostgreSQL => pgTypes.find(t=>t.jvmTypes.contains(tpe))
        case Oracle => ???
        case H2 => ???
      }).getOrElse(throw new IllegalArgumentException("Unsupported type: " + tpe.toString))
    }
  }

  case class SQLType(nativeType: String, jdbcType: Int, jvmTypes: Seq[Type])

  /*
  def ptype[A: TypeTag](f: A, flavor: DBFlavorTrait): Option[SQLType] = {
    println(typeOf[A] match { case TypeRef(_, tpe, args) => (tpe, args) })
    val targs = typeOf[A] match { case TypeRef(_, _, args) => args }
    targs.map(a=>SQLType(a, flavor)).headOption
  }*/
}

