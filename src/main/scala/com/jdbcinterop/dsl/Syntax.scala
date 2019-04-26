package com.jdbcinterop.dsl

import com.jdbcinterop.core.PSWrapper
import scala.reflect.runtime.universe._

// Comma-separated, paren-enclosed list. All values expected to be of same type.
case class SQLList[A : TypeTag](
                       values: Seq[A],
                       abortIfEmpty: Boolean,
                       open: Boolean = false,
                       maxLength: Option[Int] = None
                     ) {

  def chunks: List[SQLList[A]] = values.grouped(1000).map(g=>copy(values = g)).toList
}

case class SQLArray[A : TypeTag](values: Seq[A]) {
  val typeArg: Type = typeOf[A]
}

case class SQLTuple(values: Seq[Any])

case class UNSAFE_Direct(a: Any)


case class Setter(op: (PSWrapper, Int) => Unit)

