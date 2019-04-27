package com.jdbcinterop.core

import scala.reflect.runtime.universe._

// Comma-separated, paren-enclosed list. All values expected to be of same type.
case class SQLList[A : TypeTag](
                                 values: Seq[A],
                                 abortIfEmpty: Boolean,
                                 open: Boolean = false,
                                 maxLength: Int = 0
                               ) {

  def chunks: List[SQLList[A]]
    = if (maxLength > 0)  values.grouped(maxLength).map(g=>copy(values = g)).toList else List(this)
}

case class SQLArray[A : TypeTag](values: Seq[A]) {
  val typeArg: Type = typeOf[A]
}

case class SQLTuple(values: Seq[Any])

case class UNSAFE_Direct(a: Any)


case class Setter(op: (PSWrapper, Int) => Unit)
