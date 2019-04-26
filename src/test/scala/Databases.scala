import java.sql.{Connection, DriverManager}

import com.jdbcinterop.core.{DB, ConnectionProvider, DBFlavor, DBFlavorTrait}

class H2DB extends DB with ConnectionProvider {
  override val flavor: DBFlavorTrait = DBFlavor.H2

  override def withConnection[R](op: Connection => R): R = {
    val conn = DriverManager.getConnection("jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1")
    op(conn)
  }
}

class PGDB extends DB with ConnectionProvider {
  override val flavor: DBFlavorTrait = DBFlavor.PostgreSQL

  override def withConnection[R](op: Connection => R): R = {
    Class.forName("org.postgresql.Driver")
    val conn = DriverManager.getConnection("jdbc:postgresql://[::1]:5432/test", "peter", "getmein")
    op(conn)
  }
}