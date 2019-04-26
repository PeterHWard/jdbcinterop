import com.jdbcinterop.dsl.Setter

import scala.reflect.runtime.universe._
import scala.reflect._

def cast[A](a: Any, tt: TypeTag[A]): A = a.asInstanceOf[A]

def targ[T: TypeTag](arg: T): String = {
  val tpe0: TypeTag[_] = typeTag[String]
  val bar: Any = 1
  val s: String = 10.// cast(bar, tpe0)
  s
}

println(targ(Setter((psw, int)=>())))