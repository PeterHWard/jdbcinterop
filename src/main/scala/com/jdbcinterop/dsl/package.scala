package com.jdbcinterop

import com.jdbcinterop.core._

import scala.reflect.runtime.universe._

package object dsl {
  def openList[T: TypeTag](tuples: T*): SQLList[T] = SQLList(tuples, abortIfEmpty = false, open = true)
  def array[A : TypeTag](elems: A*): SQLArray[A] = SQLArray(elems)
  def UNSAFE_literal(text: String): UNSAFE_Direct = UNSAFE_Direct(text)
  def UNSAFE_relation(name: String): UNSAFE_Direct = {
    ???
  }
  def setter(op: (PSWrapper, Int) => Unit): Setter = Setter(op)
}
