#!/usr/bin/env node

const fs = require("fs");
const path = require("path");

function range(count) {
  return [...Array(count).keys()];
}

function tabs(count) {
  return range(count).map(_=> "  ").join("");
}

function generateInterpolation() {
  const charOffset = 65;
  const sqlMethods = range(22).map(idx=>{
    const pCount = idx + 1;
    const typeLetters =  range(pCount).map(i => String.fromCharCode(i + charOffset));
    const typeParams = typeLetters.map(l => l).join(", ");
    const valParams = typeLetters.map(l => `${l.toLowerCase()}: ${l}`).join(", ");
    const pList = `${tabs(2)}def sql[${typeParams}](${valParams}): QueryBuilder = {\n`;
    const calls = typeLetters.map(l=>{
      return `${tabs(3)}builder.addVariable[${l}](${l.toLowerCase()})\n`
        + `${tabs(3)}builder.addLiteral(strings.next())\n`
    }).join("");

    return pList 
      + `${tabs(3)}val strings = sc.parts.iterator\n`
      + `${tabs(3)}val builder = QueryBuilder.empty\n\n`
      + calls
      + `${tabs(3)}builder\n`
      + `${tabs(2)}}\n\n`
  }).join("");

  const src = `
package com.jdbcinterop.dsl

import com.jdbcinterop.dsl.QueryBuilder

package object interpolation {
  implicit class SQLHelper(val sc: StringContext) extends AnyVal {
    // Special case: No arguments 
    def sql(): QueryBuilder = {
      QueryBuilder.empty.addLiteral(sc.parts.mkString(""))
    }

    ${sqlMethods.trim()}
  }
}`;

  fs.writeFileSync(
    path.join(__dirname, "../scala/com/jdbcinterop/dsl/interpolation/package.scala"),
    src
  );
}


function generateSetters() {
  const autoTypes = [
    ["String"], 
    ["Long"], 
    ["Int"], 
    ["Double"],
    ["Date"]
  ];

  const allTypes = autoTypes;
  const setFuncs = autoTypes.map(types=>{
    const tpe = types[0];
    return `${tabs(1)}val set${tpe}: PSSetter[${tpe}] = PSSetter(\n`
      + `${tabs(2)}set = (ctx: SetValueCtx[${tpe}]) => ctx.psw.preparedStatement.set${tpe}(ctx.idx, ctx.value.asInstanceOf[${tpe}]),\n`
      + `${tabs(2)}types = Seq(${types.map(t=>`typeOf[${t}]`).join(",")}))\n`
  }).join("");

  const src = `package com.jdbcinterop.dsl

import scala.reflect.runtime.universe._
import java.sql.{Date, Types}
import org.postgresql.util.PGobject
import play.api.libs.json.JsValue

import com.jdbcinterop.core.DBFlavor.PostgreSQL
import com.jdbcinterop.core.SQLType

object Setters {
  type SetValueFunc[VALUE] = (SetValueCtx[VALUE]) => Unit
  case class PSSetter[VALUE](set: SetValueFunc[VALUE], types: Seq[Type])

  // auto-generated
${setFuncs}
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

  val allSetters: Seq[PSSetter[_]] = Seq(${allTypes.map(arr=>"set" + arr[0]).join(",")})
  def find[VALUE](tpe: Type): Option[PSSetter[VALUE]] = allSetters.find(_.types.contains(tpe)).asInstanceOf[Option[PSSetter[VALUE]]]
}`;

  fs.writeFileSync(
    path.join(__dirname, "../scala/com/jdbcinterop/dsl/Setters.scala"),
    src
  );
}


if (require.main === module) {
  generateInterpolation();
  generateSetters();
}