import com.jdbcinterop.TestMacros
import org.scalatest.FlatSpec
import com.jdbcinterop.core._


object UnitTests {

}

abstract class UnitSpec extends FlatSpec

class toSqlTypeSpec extends UnitSpec {
  import scala.reflect.runtime.universe._

  "toSqlType" should "yield nativeType of `text`" in {
    assert(SQLType(typeOf[String], DBFlavor.PostgreSQL).nativeType == "text")
  }

  it should "fail with unsupported type Foo" in {
    object Foo
    assertThrows[IllegalArgumentException](SQLType(typeOf[Foo.type], DBFlavor.PostgreSQL))
  }
}

class SQLInterpolation extends UnitSpec {
  import com.jdbcinterop.dsl._
  "SQLInterpolation" should "work" in {
    assert(sql2"${42}" == "Int")
    assert(sql2"${"foo"} ${42}" == "java.lang.String Int")
  }
}

/*
class MacroSpec extends UnitSpec {
  "macro" should "work" in {
    val arg: Any = Seq("foo")
    assert(TestMacros.apply(arg).exec() == "String")
  }
}*/