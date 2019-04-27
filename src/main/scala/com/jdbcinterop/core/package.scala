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

  object TypeMap {
    protected val pgTypes = Seq(
      TypeMap("text", Types.CLOB, Seq(typeOf[String], typeOf[java.lang.String]))
    )

    def apply(tpe: Type, flavor: DBFlavorTrait): TypeMap = {
      (flavor match {
        case PostgreSQL => pgTypes.find(t=>t.jvmTypes.contains(tpe))
        case Oracle => ???
        case H2 => ???
      }).getOrElse(throw new IllegalArgumentException("Unsupported type: " + tpe.toString))
    }
  }

  case class TypeMap(nativeType: String, jdbcType: Int, jvmTypes: Seq[Type])
}

