import com.jdbcinterop.core.SQLArray

import scala.reflect.runtime.universe._
import scala.reflect._

def cast[A](a: Any, tt: TypeTag[A]): A = a.asInstanceOf[A]

def targ[T: TypeTag](arg: T): String = {
  //(typeOf[Tuple1[Int]].typeSymbol == typeOf[Tuple2[String, Int]].typeSymbol).toString
  typeOf[T].toString
}

println(">>> " + targ(SQLArray(Seq(""))))