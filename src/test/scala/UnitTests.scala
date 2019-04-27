import org.scalatest.FlatSpec
import com.jdbcinterop.core._
import com.jdbcinterop.dsl.interpolation._

object UnitTests {

}

abstract class UnitSpec extends FlatSpec

class toSqlTypeSpec extends UnitSpec {
  import scala.reflect.runtime.universe._

  "toSqlType" should "yield nativeType of `text`" in {
    assert(TypeMap(typeOf[String], DBFlavor.PostgreSQL).nativeType == "text")
  }

  it should "fail with unsupported type Foo" in {
    object Foo
    assertThrows[IllegalArgumentException](TypeMap(typeOf[Foo.type], DBFlavor.PostgreSQL))
  }
}

/*
class SQLInterpolation extends UnitSpec {
  "SQLInterpolation" should "work" in {
    assert(sql"${42}" == "Int")
    assert(sql"${"foo"} ${42}" == "java.lang.String Int")
  }
}*/

/*
class MacroSpec extends UnitSpec {
  "macro" should "work" in {
    val arg: Any = Seq("foo")
    assert(TestMacros.apply(arg).exec() == "String")
  }
}*/