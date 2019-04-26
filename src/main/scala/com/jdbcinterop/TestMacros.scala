package com.jdbcinterop

import language.experimental.macros
import scala.reflect.macros.blackbox.Context

trait Op {
  def exec(): String
}

object Macros {
  def makeInstance_impl(c: Context)(x: c.Tree) = {
    import c.universe._
    //val tpe = param.splice.toString
    //println(tpe)
    q"""
     import _root_.com.jdbcinterop._
     class Foo extends Op {
       def exec(): String = ${x.tpe}
     }
     new Foo()
    """
  }
}

object TestMacros {
  def apply(x: Any): Op = macro Macros.makeInstance_impl
}
