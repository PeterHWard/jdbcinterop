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
    const typeParams = typeLetters.map(l => l + " : TypeTag").join(", ");
    const valParams = typeLetters.map(l => `${l.toLowerCase()}: ${l}`).join(", ");
    const pList = `${tabs(2)}def sql[${typeParams}](${valParams}): MkStatement = {\n`;
    const calls = typeLetters.map(l=>{
      return ""
        + `${tabs(3)}builder.addLiteral(strings.next())\n`
        + `${tabs(3)}builder.addVariable[${l}](${l.toLowerCase()})\n`;
    }).join("");

    return pList 
      + `${tabs(3)}val strings = sc.parts.iterator\n`
      + `${tabs(3)}val builder = QueryBuilder.empty\n\n`
      + calls
      + `${tabs(3)}if (strings.hasNext) builder.addLiteral(strings.next())\n`
      + `${tabs(3)}builder.mkStatement\n`
      + `${tabs(2)}}\n\n`
  }).join("");

  const src = `
package com.jdbcinterop.dsl

import scala.reflect.runtime.universe._

import com.jdbcinterop.core.{QueryBuilder, MkStatement}

package object interpolation {
  implicit class SQLHelper(val sc: StringContext) extends AnyVal {
    // Special case: No arguments 
    def sql: MkStatement = {
      QueryBuilder.empty.addLiteral(sc.parts.mkString("")).mkStatement
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
    ["String", "java.lang.String"], 
    ["Long", "java.lang.Long"], 
    ["Int"], 
    ["Double", "java.lang.Double"],
    ["Date"]
  ];

  const allTypes = autoTypes.concat([
    ["Setter"],
    ["JsValue"],
    ["Null"],
    ["Array"]]);

  const typeArgs = [].concat.apply([], autoTypes)

  const withTypeArgs = (tpe) => typeArgs.map(t=>`typeOf[${tpe}[${t}]]`).join(",");

  const setFuncs = autoTypes.map(types=>{
    const tpe = types[0];
    return `${tabs(1)}val set${tpe}: PSSetter[${tpe}] = PSSetter(\n`
      + `${tabs(2)}set = (ctx: SetValueCtx[${tpe}]) => ctx.psw.preparedStatement.set${tpe}(ctx.idx, ctx.valueAs[${tpe}]),\n`
      + `${tabs(2)}types = Seq(${types.map(t=>`typeOf[${t}]`).join(",")}))\n`
  }).join("");

  const src = `package com.jdbcinterop.core

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
${setFuncs}
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
      types = Seq(${withTypeArgs("SQLArray")}))
  val setJsValue: PSSetter[JsValue] = PSSetter(
    set = (ctx: SetValueCtx[JsValue]) => ctx.psw.setJson(ctx.idx, ctx.valueAs[JsValue]),
    types = Seq(typeOf[JsValue], typeOf[JsObject]))

  val allSetters: Seq[PSSetter[_]] = Seq(${allTypes.map(arr=>"set" + arr[0]).join(",")})
  def find[VALUE](tpe: Type): Option[PSSetter[VALUE]] = allSetters.find(_.types.contains(tpe)).asInstanceOf[Option[PSSetter[VALUE]]]
}`;

  fs.writeFileSync(
    path.join(__dirname, "../scala/com/jdbcinterop/core/Setters.scala"),
    src
  );
}


if (require.main === module) {
  generateInterpolation();
  generateSetters();
}