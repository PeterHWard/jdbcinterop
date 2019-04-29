# JDBC Interop

A small library designed to smooth over some of the rough edges when working with JDBC from Scala.

# Warning
This library is not stable nor thoroughly tested. Do not use in production. 

# Quickstart Example
```scala
import com.jdbcinterop.db.{DB, DBFlavor}
import com.jdbcinterop.dsl._
import com.jdbcinterop.dsl.interpolation._

val db = new DB {
  override val flavor: DBFlavorTrait = DBFlavor.H2
  override def withConnection[R](op: Connection => R): R = {
    val conn = DriverManager.getConnection("jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1")
    op(conn)
  }
}

val firstName = "GINA"
val lastName = "DEGENERES"

val query = sql"""SELECT count(*) films
                  FROM actor AS a
                  JOIN film_actor AS fa USING (actor_id)
                  GROUP BY actor_id, first_name, last_name
                  WHERE first_name = $firstName
                  AND last_name = $lastName"""

val result = db.executeQuery(query)(_.getInt).head // 42
```


# What This is Not
JDBC Interop is not an ORM or functional abstraction for working with RDBMS. If that is what you are looking for consider:

- [Hibernate](https://en.wikipedia.org/wiki/Hibernate_(framework))
- [Quill](https://github.com/getquill/quill)
- [ScalikeJDBC](http://scalikejdbc.org/)
- [Slick](http://slick.lightbend.com/)

It is not a parser or code generator. It simply provides sugar for producing prepared statements. If you wish to compose
SQL programmatically consider Quill.

It is not a connection pool manager. The `DB` API closes connections and prepared statements automatically to help prevent
connection leaks. However, should be used in conjunction with not as an alternative to a framework like 
[HikariCP](https://github.com/brettwooldridge/HikariCP).


# TODO
- DB API


# Interpolation DSL
JDBC Interop's key feature. Allows variables to be placed directly in a SQL string and hopefully "does the right thing".

```scala
val db: DB = ...
val name = "Eve"
val query = sql"SELECT * FROM users WHERE first_name = $name"
db.executeQuery(query)(rsw=> /* code to map over result set here */) ...
```

This might look scary, but note the `sql` prefix. `query` is not a string but a special function that gets consumed by
one of `DB`'s exec methods - `executeQuery()` in this example. Under the hood something like this is happening:

```scala
val conn: java.sql.Connection = ...
val ps = conn.prepare("SELECT * FROM users WHERE first_name = ?")   // the variable `name` is replaced with `?`
ps.setString(1, name)                                               // we safely pass the value of `name` here
```

## SQL Injection
JDBC Interop uses JDBC prepared statements to generate queries. The `sql` string interpolator is doing more than naive
variable/text substition. Variables passed to `sql` interpolator methods are either converted to a single ? or, in the
case of tuples and lists, used to produce tuple/list constants - also properly ?-paremeterized.

For cases where direct string interpolation is required, JDBC Interop offers special unsafe methods and classes whose
names start with UNSAFE. These *do* insert text directly into the query string and are subject to SQL injection if misused.


### Eample - Single Value
```scala 
sql"UPDATE foo SET bar = ${42}"
```

effectively becomes:

```scala
val ps = conn.prepare("UPDATE foo SET bar = ?")   
ps.setInt(1, 42)  
```

### Eample - Tuple
```scala 
sql"INSERT INTO foo VALUES ${(1, "bar")}"
```

effectively becomes:

```scala
val ps = conn.prepare("INSERT INTO foo VALUES (?, ?)")  
ps.setInt(1, 1)
ps.setString(2, "bar")  
```

### Eample - List
```scala 
sql"SELECT * FROM foo WHERE id IN ${list(Seq(1,2,3,...)}"
```

effectively becomes:

```scala
val ps = conn.prepare("ISELECT * FROM foo WHERE id IN (?,?,?,...)")  
ps.setInt(1, 1)
ps.setInt(2, 2)
ps.setInt(3, 3)
...
```

## Lists
Tuples and lists produce the same syntatic result. 

```scala
sql"SELECT * FROM foo WHERE id IN ($1,$2,$3)"           // values passed separately
sql"SELECT * FROM foo WHERE id IN ${(1,2,3)}"           // Scala Tuple3
sql"SELECT * FROM foo WHERE id IN ${list(Seq(1,2,3))}"  // DSL method accepting Seq
```

Result: 

```sql
SELECT * FROM foo WHERE id IN (?,?,?)
```

However lists are 
- variable length
- all values of the same type
- incur greater runtime overhead as a `?` has to be inserted dynamically for each item
 
The ability to produce constants from arbitrarily long lists is probably JDBC Interop's most useful feature.
Normally, prepared statements require knowing the length of lists ahead of time.

### list
Create an SQLList[A] instance from a Seq[A] with default configuration.

```scala
sql"SELECT * FROM foo WHERE id IN ${list(Seq(1,2,3))}"  
```

### open
An "open" list can be created:

```scala
INSERT INTO foo (id, flag) VALUES ${open(
  (1, "a"),
  (2, "b"),
  (3, "c")
)}
```

This generates:

```sql
INSERT INTO foo (id, flag) VALUES
  (?, ?),
  (?, ?),
  (?, ?)
```

### SQLList[A] Class
The `SQLList[A]` class can be instantiated directly affording several config options:

```scala
SQLList[A](
  values: Seq[A]                  // the source seq, required
  abortIfEmpty: Boolean,          // See [TBA]
  open: Boolean = false,          // if true enclosing parens omitted
  maxLength: Option[Int] = None   // See [TBA]
  autoChunk: Boolean = false      // See [TBA]
)
```

### Empty List Handling
In many contexts an empty list constant is a syntax error. `SQLList#abortIfEmpty` sort-circuits query execution
if an empty list is encountered. Exec methods like `execQuery()` mapping over a result set will return `Nil` while 
methods returning `Boolean` will return `false`. This enables "safe" execution when creating a list from a sequence type 
that could be empty.

If `SQLList#abortIfEmpty` is `false` (the default), the query execution is attempted possibly resulting in an exception or
other unexpected behavior.

In general, its best practice for your code to guard against empty lists being passed. In the case of a WHERE IN,
returning `Nil` seems intuitive. But in most other situations another strategy is probably best. Validating user inputs
to ensure at least one value is given, for example.

### Maximum Length for Lists
Some databases impose a limit on the size of list constants (1000 for Oracle 11g). By setting the `SQLList#maxLength`
property you may specify that an exception is thrown if a list of greater than `maxLength` would be created.

If `SQLList#autoChunk` is true, queries with lists longer than `maxLength` will be split into chunks. This feature is only
supported for queries where a single list of greater than `SQLList#maxLength` exits. It is incumbent on the user to ensure
the rest of the query is unaffected by the "chunkification".

As with empty lists, lists that exceed a certain bounds should in most cases be addressed by user code rather than rely
on JDBC interop to automagically gloss over problems. For long where in lists where the rest of the query is static they 
can prove handy though.

### SQL Array
Currently sequence types passed directy are not treated as arrays. This may change in future. To statically define an 
array via the DSL use:

```scala
sql"UPDATE foo SET arr = ${array("X", "Y", "Z")} WHERE ..." 
 ```

If you wish to create an array from a sequence type use the `SQLArray[A]` class.

```scala 
sql"UPDATE foo SET arr = ${SQLArray(Seq("X", "Y", "Z"))} WHERE ..." 
```

## UNSAFE Methods and Classes
The DSL offers an escape hatch where regular string interpolation needs to be achived. These methods and classes are "unsafe"
meaning the values they produce are inserted as-is with no sanitization into the query. They must not be feed data from user inputs
as doing so will create an SQL injection vector.

### UNSAFE_direct
The `UNSAFE_direct()` DSL method allows a variable to be inserted into the SQL string as-is.

```scala
val table = if (isDev) "dev_table" else "prod_table"
val q = sql"SELECT * FROM ${UNSAFE_direct(table)} WHERE ..."
```

The text of `q` will be (assuming isDev is true)

```sql 
SELECT * FROM dev_table WHERE ...
```

### UNSAFE_relation
Same as `UNSAFE_direct` except the string is double-quoted.

```scala
val table = if (isDev) "dev_table" else "prod_table"
val q = sql"SELECT * FROM ${UNSAFE_direct(table)} WHERE ..."
```

becomes

sql```SELECT * FROM "dev_table" WHERE ...```

As with `UNSAFE_direct`, `UNSAFE_relation` does not sanitize the input and must never be called with user input text/data.
JDBC does not provide a safe way to set column or other relation names dynamically.

## Setter
The `Setter` class provides "low level" access to the `DB` API's `PSWrapper` and underlying prepared statement so that a value can be
set directly. This is useful when dealing with types not supported by JDBC interop.

In this silly example we translate a boolean into "Y"/"N" to demonstrate:

```scala
val setter = Setter((psw, idx)=>psw.preparedStatement.setString(idx, if (true) "Y" else "N"))
val query = sql"""UPDATE foo SET is_active = $setter"""
...
```

The `Setter` constructor takes a "op" single argument. A function that takes the `PSWrapper` and the `Int` index of the value.
It is import that one and only one set method gets called otherwise query execution will fail. In other words, `Setter`
cannot be used to conditionally avoid setting a value.

## Options and null
Options are treated intuitively.

User code:
```scala
val maybeString: Option[String] = Some("foo") // ensure type argument for Option is defined - `String` here
val q = sql"UPDATE some_table SET text_column = $maybeString WHERE ..."   
...
```

Psuedo code illustrating logic of JDBC Interop internals:
```scala
val ps = conn.prepare(...)
val idx = ...
if (maybeString.isDefined) ps.setString(idx, maybeString.get)
else ps.setNull(idx, java.sql.Types.NULL)
```

One quirk using Options is this fails:

```scala 
sql"UPDATE foo SET bar = ${None}"
```

as the type parameter must still be known. Instead use:

```scala 
sql"UPDATE foo SET bar = ${None.asInstanceOf[Option[Int]]}"
```

When updating or inserting, responsibility rests with the user to ensure Options are only passed where the column is nullable.

If the JDBC Interop type discriminator encounters a naked `null` an exception is thrown. We are in Scala land where one expects 
Options any time a value could be nonexistant. Furthermore, unboxed JVM value types (i.e., those extending `AnyVal` such as Int) 
cannot be null whereas columns of any type, even numeric types, may nullable in typical RDBMS. Treating None as null provides a consistent, 
Scala-idiomatic way to encode nullability.

## Json
Variables of type [`play.api.libs.json.JsValue`](https://github.com/playframework/play-json) can be passed where JSON is expected.

```scala
val data = Json.obj("foo" -> 42) // create JsObject
val query = sql"""UPDATE raw_data SET json = $data"""  
```

Currently PostgreSQL is the only database where automatic JSON handeling is supported. If `JsValue` is passed to an
unsupported database an exception is thrown.

## Supported Types
Apart from special types provided by JDBC Interop, the following well-known types are supported:

| JVM Type      | Set With                       | Notes                          |
| ------------- | ------------------------------ | ------------------------------ | 
| String        | setString                      |                                |
| Boolean       | setBoolean                     |                                |
| Int           | setInt                         |                                | 
| Long          | setLong                        |                                |
| Double        | setDouble                      |                                |
| java.sql.Date | setDate                        |                                |
| Option[T]     | [depends on T]                 | See [Options and null]         | 
| JsValue       | setObject                      | [only supported by PostgreSQL] |


# DB API

## Scopes 
`exec` methods can be called directly from the `DB` instance. Alternatively "scopes" session and transaction can be created. 

### withSession
Reuses the same database connection for each query. Handy if you wish to access the same temp tables from multiple queries
when using PostgreSQL, where temp tables are disposed after each session. 

Unlike `withTransaction`, `withConnection` commits each query. However, nested transaction scopes can be created 
within a connection scope. 

**Note:** If you are using Hikari or other connection manager or if the databse is configured to close idle sessions 
automatically: `withSessions` provides no means (such as polling the server) to force the connection to "stay alive". 

### withTransaction
All queries executed in this scope form part of the same transaction. As with `withSession`, `withTransaction` has no 
means to prevent a connection manager or the database server from closing the session before the transaction is complete. 
Queries forming part of a transaction should be performed serially with no significant pauses between queries.


# Limitations

## Prepared Statement API

## Comile-Time Type Information
To facilitate "reusable" queries as well as macro-generated queries in future, the types of variables passed to the 
interpolator must be defined at compile time. For example, to pass None directly:

```scala sql"UPDATE foo SET bar = ${None.asInstanceOf[Option[Int]]}"```

## 22 Variable Limit
Typically extensions to `StringContext` are variadic methods 
(see: [Scala Docs](https://docs.scala-lang.org/overviews/core/string-interpolation.html)):

```scala 
implicit class FooHelper(val sc: StringContext) extends AnyVal {
  def foo(args: Any*): Foo = ???
}
``` 

However because the `sql` interpolator requires knowledge of each argument type, overloads must be synthesized for
every arity supported.

```scala 
def sql[A : TypeTag, B : TypeTag](a: A, b: B) ... // arity-2 example
```

For now methods are only synthesized up to arity-22. 22 was chosen as it is the max tuple size, though technically methods
taking longer arguments lists could be added. 


# Async?
The `DB` API like JDBC is synchronous. It would be trivial create versions of the API's exec methods that return Futures.
But in my opinion a true asynchronous database access framework requires async drivers and cannot easy be built atop JDBC. 
At any rate, to be effective async APIs require architectural considerations beyond the scope of this project. 


## Proposed Features
### Relations Whitelist

