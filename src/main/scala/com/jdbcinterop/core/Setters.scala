package com.jdbcinterop.core

import scala.reflect.runtime.universe._
import java.sql.{Date}

import play.api.libs.json.{JsValue, JsObject}

case class SetValueCtx[VALUE](psw: PSWrapper, idx: Int, flavor: DBFlavorTrait, value: Any) {
  def valueAs[T]: T = value.asInstanceOf[T]
}

object Setters {
  type SetValueFunc[VALUE] = (SetValueCtx[VALUE]) => Unit
  case class PSSetter[VALUE](set: SetValueFunc[VALUE], types: Seq[Type])

  // auto-generated
  val setString: PSSetter[String] = PSSetter(
    set = (ctx: SetValueCtx[String]) => ctx.psw.preparedStatement.setString(ctx.idx, ctx.valueAs[String]),
    types = Seq(typeOf[String],typeOf[java.lang.String]))
  val setLong: PSSetter[Long] = PSSetter(
    set = (ctx: SetValueCtx[Long]) => ctx.psw.preparedStatement.setLong(ctx.idx, ctx.valueAs[Long]),
    types = Seq(typeOf[Long],typeOf[java.lang.Long]))
  val setInt: PSSetter[Int] = PSSetter(
    set = (ctx: SetValueCtx[Int]) => ctx.psw.preparedStatement.setInt(ctx.idx, ctx.valueAs[Int]),
    types = Seq(typeOf[Int]))
  val setDouble: PSSetter[Double] = PSSetter(
    set = (ctx: SetValueCtx[Double]) => ctx.psw.preparedStatement.setDouble(ctx.idx, ctx.valueAs[Double]),
    types = Seq(typeOf[Double],typeOf[java.lang.Double]))
  val setDate: PSSetter[Date] = PSSetter(
    set = (ctx: SetValueCtx[Date]) => ctx.psw.preparedStatement.setDate(ctx.idx, ctx.valueAs[Date]),
    types = Seq(typeOf[Date]))

  // hard-coded
  val setSetter: PSSetter[Setter] = PSSetter(
    set = (ctx: SetValueCtx[Setter]) => ctx.psw.setSetter(ctx.idx, ctx.valueAs[Setter]),
    types = Seq(typeOf[Setter]))
  val setNull: PSSetter[Any] = PSSetter(
    set = (ctx: SetValueCtx[Any]) => ctx.psw.setNull(ctx.idx),
    types = Seq())
  val setNone: PSSetter[Any] = PSSetter(
    set = (ctx: SetValueCtx[Any]) => ctx.psw.setNull(ctx.idx),
    types = Seq(typeOf[scala.None.type]))
  val setArray: PSSetter[SQLArray[_]] = PSSetter(
    set = (ctx: SetValueCtx[SQLArray[_]]) => ctx.psw.setArray(ctx.idx, ctx.valueAs[SQLArray[_]]),
      types = Seq(typeOf[SQLArray[String]],typeOf[SQLArray[java.lang.String]],typeOf[SQLArray[Long]],typeOf[SQLArray[java.lang.Long]],typeOf[SQLArray[Int]],typeOf[SQLArray[Double]],typeOf[SQLArray[java.lang.Double]],typeOf[SQLArray[Date]]))
  val setJsValue: PSSetter[JsValue] = PSSetter(
    set = (ctx: SetValueCtx[JsValue]) => ctx.psw.setJson(ctx.idx, ctx.valueAs[JsValue]),
    types = Seq(typeOf[JsValue], typeOf[JsObject]))

  val allSetters: Seq[PSSetter[_]] = Seq(setString,setLong,setInt,setDouble,setDate,setSetter,setJsValue,setNull,setArray)
  def find[VALUE](tpe: Type): Option[PSSetter[VALUE]] = allSetters.find(_.types.contains(tpe)).asInstanceOf[Option[PSSetter[VALUE]]]
}