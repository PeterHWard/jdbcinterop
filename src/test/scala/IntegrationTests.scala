import org.scalatest.FlatSpec
import play.api.libs.json.{JsObject, Json}
import com.jdbcinterop.core.{SQLList, UNSAFE_Direct}
import com.jdbcinterop.dsl._
import com.jdbcinterop.dsl.interpolation._

object IntegrationTests {

}


object HandledException {
  def handle(op: () => Unit): Unit = {
    try {
      op()
    } catch {
      case he: HandledException => ()
    }
  }
}

class HandledException extends RuntimeException


abstract class IntegrationSpec extends FlatSpec

object Helper {
  val db = new PGDB
  import db.{execQuery => q, execUpdate => u}

  def withTable(name: String, op: UNSAFE_Direct  => Unit): Unit = {
    Symbol(name).synchronized {
      db.raw(s"DROP TABLE IF EXISTS $name")
      name match {
        case "a" | "aa" =>
          db.raw(
            s"""CREATE TABLE $name (
              id int PRIMARY KEY,
              text_col text
            )""".stripMargin)
          db.raw(
            s"""INSERT INTO $name (id, text_col) VALUES
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

      op(UNSAFE_Direct(name))
    }
  }
}


class DBSpec extends IntegrationSpec {
  import Helper._

  "DB" should "roll back update" in {
    withTable("aa", a => {
      db.exec(sql"UPDATE $a SET text_col = '---'")
      assert(db.execQuery(sql"SELECT * FROM a WHERE text_col = 'xyz'")(_ => true).toSeq.isEmpty)

      HandledException.handle(() => {
        db.withTransaction(tx => {
          tx.exec(sql"UPDATE $a SET text_col = 'xyz' WHERE id  = 1")
          tx.exec(sql"UPDATE $a SET text_col = 'abc' WHERE id  = 2")
          throw new HandledException()
        })
      })

      assert(db.execQuery(sql"SELECT * FROM $a WHERE text_col = 'xyz'")(_ => true).toSeq.isEmpty)
      assert(db.execQuery(sql"SELECT * FROM $a WHERE text_col = 'abc'")(_ => true).toSeq.isEmpty)
      assert(db.execQuery(sql"SELECT * FROM $a WHERE text_col = '---'")(_ => true).toSeq.nonEmpty)
    })
  }

  it should "complete transaction" in {
    withTable("aa", a => {
      db.withTransaction(tx => {
        tx.exec(sql"UPDATE $a SET text_col = 'xyz' WHERE id  = 1")
        tx.exec(sql"UPDATE $a SET text_col = 'abc' WHERE id  = 2")
      })

      assert(db.execQuery(sql"SELECT * FROM $a WHERE text_col = 'xyz'")(_ => true).toSeq.length == 1)
      assert(db.execQuery(sql"SELECT * FROM $a WHERE text_col = 'abc'")(_ => true).toSeq.length == 1)
    })
  }

  it should "persist session" in {
    db.withSession(session => {
      session.exec(sql"CREATE TEMP TABLE tmp (a text)")
      session.exec(sql"INSERT INTO tmp VALUES ('xyz')")
      val res = session.execQuery(sql"SELECT * FROM tmp")(_.getString("a").get).head
      assert(res == "xyz")
    })
  }
}


class SQLInterpolationSpec extends IntegrationSpec {

  import Helper._
  import db.{execQuery => q, execUpdate => u}

  "SQLInterpolation" should "find id 1" in {
    withTable("a", _ => {
      assert(db.execQuery(sql"""SELECT id FROM a WHERE id = ${1}""")(_.getInt("id")).head == 1)
      assert(db.execQuery(sql"""SELECT id FROM a WHERE text_col = ${"1"}""")(_.getInt("id")).head == 1)
    })
  }

  it should "insert a new record" in {
    withTable("a", _ => {
      assert(u(sql"""INSERT INTO a VALUES ${(4, "s4")}""") == 1)
    })
  }

  it should "insert 3 new records" in {
    withTable("a", _ => {
      assert(u(
        sql"""INSERT INTO a VALUES ${
          openList(
            (5, "5"),
            (6, "6"),
            (7, "7")
          )
        }""") == 3)

      db.execQuery(sql"""SELECT * FROM a""")(r => (r.getInt("id"), r.getString("text_col"))).foreach(t => {
        assert(t._2.get == t._1.toString)
      })
    })
  }

  it should "set null" in {
    withTable("nullable", _ => {
      assert(db.execUpdate(sql"""INSERT INTO nullable VALUES ${(None.asInstanceOf[Option[Int]], Option[String](null))}""") == 1)
    })
  }

  it should "take an Option" in {
    withTable("nullable", _ => {
      assert(db.execUpdate(sql"""INSERT INTO nullable VALUES ${openList((8, Some("8")))}""") == 1)
      assert(db.execQuery(sql"""SELECT * FROM nullable WHERE int_col = 8""")(_.getString("text_col").get).head == "8")
      assert(db.execUpdate(sql"""INSERT INTO nullable VALUES ${openList((None.asInstanceOf[Option[Int]], "null"))}""") == 1)
    })
  }

  it should "find 3 results" in {
    withTable("a", _ => {
      assert(db.execQuery(sql"""SELECT id FROM a WHERE id IN ${(1, 2, 3)}""")(_.getInt("id")).toSeq.length == 3)
    })
  }

  it should "take an array" in {
    withTable("b", _ => {
      db.raw("TRUNCATE TABLE b")

      assert(u(
        sql"""INSERT INTO b VALUES ${
          openList(
            (1, array("1a", "1b")),
            (2, array("2a", "2b")),
            (3, array("3a", "3b")),
          )
        }""") == 3)

      assert(q(sql"""SELECT arr FROM b WHERE id = 2""")(rsw => {
        rsw.getArray[Any]("arr")
      }).head.head == "2a")
    })
  }

  it should "find 3" in {
    assert(db.execQuery(sql"""SELECT * FROM b WHERE id IN ${SQLList(Seq(1, 2, 3), abortIfEmpty = false)}""")(_ => true).toSeq.length == 3)
  }

  it should "set text_col to 'foo'" in {
    withTable("a", _ => {
      assert(db.execUpdate(
        sql"""UPDATE a SET text_col = ${
          setter((psw, idx) => {
            psw.preparedStatement.setString(idx, "foo")
          })
        } WHERE id = 3""") == 1)

      assert(db.execQuery(sql"""SELECT * FROM a WHERE id = 3""")(_.getString("text_col")).head.get == "foo")
    })
  }

  it should "take a literal" in {
    withTable("a", _ => {
      assert(db.execQuery(sql"""SELECT * FROM ${UNSAFE_literal("a")}""")(_ => ()).toSeq.nonEmpty)
    })
  }

  it should "take json" in {
    withTable("j", _ => {
      val j = Json.obj("key" -> 42)
      u(sql"""UPDATE j SET json = $j WHERE id = 1""")
      val jval = q(sql"""SELECT json FROM j WHERE id = 1""")(_.getJsValue("json")).head
      assert((jval.as[JsObject] \ "key").as[Int] == 42)
    })
  }

  it should "split long list" in {
    withTable("a", _ => {
      u(sql"INSERT INTO a VALUES (${3001}, ${"You got me"})")
      val ids = SQLList(1 to 3500, abortIfEmpty = true, maxLength = 1000)
      assert(ids.chunks.length == 4)
      assert(q(
        sql"""
   SELECT * FROM a WHERE id IN $ids
   AND text_col = 'You got me'""")
      (_.getString("text_col").get).head == "You got me")
    })
  }
}