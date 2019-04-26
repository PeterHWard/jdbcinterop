package com.jdbcinterop.dsl

import scala.reflect.runtime.universe._
import java.sql.{Date, Types}

import org.postgresql.util.PGobject
import play.api.libs.json.JsValue
import com.jdbcinterop.core.DBFlavor.PostgreSQL
import com.jdbcinterop.core.{SQLType, SetValueCtx}

object Setters {
  type SetValueFunc[VALUE] = (SetValueCtx[VALUE]) => Unit
  case class PSSetter[VALUE](set: SetValueFunc[VALUE], types: Seq[Type])

  // auto-generated
  val setString: PSSetter[String] = PSSetter(
    set = (ctx: SetValueCtx[String]) => ctx.psw.preparedStatement.setString(ctx.idx, ctx.value.asInstanceOf[String]),
    types = Seq(typeOf[String]))
  val setLong: PSSetter[Long] = PSSetter(
    set = (ctx: SetValueCtx[Long]) => ctx.psw.preparedStatement.setLong(ctx.idx, ctx.value.asInstanceOf[Long]),
    types = Seq(typeOf[Long]))
  val setInt: PSSetter[Int] = PSSetter(
    set = (ctx: SetValueCtx[Int]) => ctx.psw.preparedStatement.setInt(ctx.idx, ctx.value.asInstanceOf[Int]),
    types = Seq(typeOf[Int]))
  val setDouble: PSSetter[Double] = PSSetter(
    set = (ctx: SetValueCtx[Double]) => ctx.psw.preparedStatement.setDouble(ctx.idx, ctx.value.asInstanceOf[Double]),
    types = Seq(typeOf[Double]))
  val setDate: PSSetter[Date] = PSSetter(
    set = (ctx: SetValueCtx[Date]) => ctx.psw.preparedStatement.setDate(ctx.idx, ctx.value.asInstanceOf[Date]),
    types = Seq(typeOf[Date]))

  // hard-coded
  val setSetter: PSSetter[Setter] = PSSetter(
    set = (ctx: SetValueCtx[Setter]) => ctx.value.asInstanceOf[Setter].op(ctx.psw),
    types = Seq(typeOf[Setter]))
  val setNull: PSSetter[Any] = PSSetter(
    set = (ctx: SetValueCtx[Any]) => ctx.psw.preparedStatement.setNull(ctx.idx, Types.NULL),
    types = Seq())
  /* Handle like list
    val setOption: PSSetter[Option[_]] = PSSetter(
    set = (ctx: SetValueCtx[Option[_]]) => {
      if (ctx.value.isDefined) {
        ???
      } else {
        setNull.set(ctx)
      }
    },
    types = Seq(typeOf[Option[_]]))
  )*/
  val setArray: PSSetter[SQLArray[_]] = PSSetter(
    set = (ctx: SetValueCtx[SQLArray[_]]) => {
      val ps = ctx.psw.preparedStatement
      val sqlArray = ctx.value.asInstanceOf[SQLArray[_]]
      val arr = ps.getConnection.createArrayOf(
        SQLType(sqlArray.typeArg, ctx.flavor).nativeType,
        sqlArray.asInstanceOf[SQLArray[_]].values.map(_.asInstanceOf[AnyRef]).toArray)
      ps.setArray(ctx.idx, arr)},
      types = Seq(typeOf[SQLArray[_]]))
  val setJsValue: PSSetter[JsValue] = PSSetter(
    set = (ctx: SetValueCtx[JsValue]) => {
      val ps = ctx.psw.preparedStatement
      ctx.flavor match {
        case PostgreSQL =>
          var po = new PGobject()
          po.setType("json")
          po.setValue(ctx.value.asInstanceOf[JsValue].toString())
          ps.setObject(ctx.idx, po)
        case _ => throw new IllegalArgumentException("No JSON support for " + ctx.flavor)
      }},
    types = Seq(typeOf[SQLArray[_]]))

  val allSetters: Seq[PSSetter[_]] = Seq(setString,setLong,setInt,setDouble,setDate)
  def find[VALUE](tpe: Type): Option[PSSetter[VALUE]] = allSetters.find(_.types.contains(tpe)).asInstanceOf[Option[PSSetter[VALUE]]]
}