import org.scalatest.FlatSpec
import play.api.libs.json.{JsObject, Json}
import com.jdbcinterop.dsl._

object IntegrationTests {

}


abstract class IntegrationSpec extends FlatSpec

class DBSpec extends IntegrationSpec {
  val db = new PGDB
  /*"DB" should "return true when `raw` invoked for valid query" in {
    assert(db.raw("CREATE TABLE IF NOT EXISTS foo (id int)"))
  }*/
}

class SQLInterpolationSpec extends IntegrationSpec {
  val db = new PGDB
  import db.{execQuery => q, execUpdate => u}

  private def withTable(name: String): Unit = {
    db.raw(s"DROP TABLE IF EXISTS $name")
    name match {
      case "a" =>
        db.raw("""CREATE TABLE a (
          id int PRIMARY KEY,
          text_col text
        )""".stripMargin)
        db.raw("""INSERT INTO a (id, text_col) VALUES
          (1, '1'),
          (2, '2'),
          (3, '3')""".stripMargin)
      case "b" =>
        db.raw("""CREATE TABLE b (id int PRIMARY KEY, arr text[])""")
        db.raw(
          """INSERT INTO b VALUES
            (1, ARRAY['1a', '1b']),
            (2, ARRAY['2a', '2b']),
            (3, ARRAY['3a', '3b'])
          """.stripMargin)
      case "nullable" => db.exec(sql"""CREATE TABLE nullable (int_col int, text_col text)""")
      case "j" =>
        db.raw("CREATE TABLE j (id int PRIMARY KEY, json JSON)")
        u(sql"""INSERT INTO j VALUES (1, ${Json.obj("foo" -> 42)})""")
    }
  }

  "SQLInterpolation" should "find id 1" in {
    withTable("a")
    assert(db.execQuery(sql"""SELECT id FROM a WHERE id = ${1}""")(_.getInt("id")).head == 1)
    assert(db.execQuery(sql"""SELECT id FROM a WHERE text_col = ${"1"}""")(_.getInt("id")).head == 1)
  }

  it should "insert a new record" in {
    withTable("a")
    assert(u(sql"""INSERT INTO a VALUES ${(4, "4")}""") == 1)
  }

  it should "insert 3 new records" in {
    withTable("a")
    assert(u(sql"""INSERT INTO a VALUES ${openList(
      (5, "5"),
      (6, "6"),
      (7, "7")
    )}""") == 3)

    db.execQuery(sql"""SELECT * FROM a""")(r=>(r.getInt("id"), r.getString("text_col"))).foreach(t=>{
      assert(t._2.get == t._1.toString)
    })
  }

  it should "set null" in {
    withTable("nullable")
    assert(db.execUpdate(sql"""INSERT INTO nullable VALUES ${(null, null)}""") == 1)
  }

  it should "take an Option" in {
    withTable("nullable")
    assert(db.execUpdate(sql"""INSERT INTO nullable VALUES ${openList((8, Some("8")))}""") == 1)
    assert(db.execQuery(sql"""SELECT * FROM nullable WHERE int_col = 8""")(_.getString("text_col").get).head == "8")
    assert(db.execUpdate(sql"""INSERT INTO nullable VALUES ${openList((None, "null"))}""") == 1)
  }

  it should "find 3 results" in {
    withTable("a")
    assert(db.execQuery(sql"""SELECT id FROM a WHERE id IN ${(1,2,3)}""")(_.getInt("id")).toSeq.length == 3)
  }

  it should "take an array" in {
    withTable("b")
    db.raw("TRUNCATE TABLE b")

    assert(u(sql"""INSERT INTO b VALUES ${openList(
      (1, array("1a", "1b")),
      (2, array("2a", "2b")),
      (3, array("3a", "3b")),
    )}""") == 3)

    assert(q(sql"""SELECT arr FROM b WHERE id = 2""")(rsw=>{
      rsw.getArray[Any]("arr")
    }).head.head == "2a")
  }

  it should "find 3" in {
    assert(db.execQuery(sql"""SELECT * FROM b WHERE id IN ${SQLList(Seq(1,2,3), abortIfEmpty = false)}""")(_=>true).toSeq.length == 3)
  }

  it should "set text_col to 'foo'" in {
    withTable("a")
    assert(db.execUpdate(sql"""UPDATE a SET text_col = ${setter((psw, idx)=>{
      psw.preparedStatement.setString(idx, "foo")
    })} WHERE id = 3""") == 1)

    assert(db.execQuery(sql"""SELECT * FROM a WHERE id = 3""")(_.getString("text_col")).head.get == "foo")
  }

  it should "take a literal" in {
    withTable("a")
    assert(db.execQuery(sql"""SELECT * FROM ${UNSAFE_literal("a")}""")(_=>()).toSeq.nonEmpty)
  }

  it should "take json" in {
    withTable("j")
    val j = Json.obj("key" -> 42)
    u(sql"""UPDATE j SET json = $j WHERE id = 1""")
    val jval = q(sql"""SELECT json FROM j WHERE id = 1""")(_.getJsValue("json")).head
    assert((jval.as[JsObject] \ "key").as[Int] == 42)
  }
}
