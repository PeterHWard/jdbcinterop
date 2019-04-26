package com.jdbcinterop.core

class SQLException(val message: String) extends RuntimeException(message)

class Abort extends SQLException("[should be handled internally]")

class ZeroLengthList extends SQLException("List must have at least one element")

class OverLongList(actual: Int, maximum: Int) extends SQLException(s"Give list with $actual items. Maximun is $maximum")
