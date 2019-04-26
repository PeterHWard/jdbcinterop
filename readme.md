JDBC Interop

A small library designed to smooth over some of the rough edges when working with JDBC from Scala.

# Quickstart Example
scala```
  import com.jdbcinterop.db.{DB, DBFlavor}
  import com.jdbcinterop.dsl._

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

It is not a parser or code generator. It simply provides sugar for producing prepared statements. If you wich to compose
SQL programmatically consider Quill.

# TODO
- DB API

# Interpolation DSL
JDBC Interop's key feature. Allows variables to be placed directly in queries and hopefully "does the right thing".

scala```
  val db: DB = ...
  val name = "Eve"
  val query = sql"SELECT * FROM users WHERE first_name = $name"
  db.executeQuery(query)(rsw=> /* code to map over result set here */) ...
```

This might look scary, but note the `sql` prefix. `query` is not a string but a special function that gets consumed by
one of `DB`'s exec methods `executeQuery()`` in this example. Under the hood something like this is happening:

scala```
  val conn: java.sql.Connection = ...
  val ps = conn.prepare("SELECT * FROM users WHERE first_name = ?")   // the variable `name` is replaced with `?`
  ps.setString(1, name)                                               // we safely pass the value of `name` here
```

## SQL Injection
JDBC Interop uses JDBC prepared statements of generate queries. The `sql` string interpolation is doing more than naive
variable/text substition. Varaibles passed to the `sql` interpolation method are either converted to a single ? or, in the
case of tuples, used to produce list constants - also properly ?-paremeterized.

For cases where direct string interpolation is required, JDBC Interop offers special unsafe methods and classes whose
names start with UNSAFE. These *do* insert text directly into the query string and are subject to SQL injection.

## Lists and Arrays
Tuples as well as the DSL's `SQLList[A]`` class are converted to SQL list constants. The following

scala```
  sql"SELECT * FROM foo WHERE id IN ($1,$2,$3)"
  sql"SELECT * FROM foo WHERE id IN ${(1,2,3)}"
  sql"SELECT * FROM foo WHERE id IN ${SQLList(Seq(1,2,3), false)}" // SQLList configuration explained below
```

all translate to:

sql```SELECT * FROM foo WHERE id IN (?,?,?)```

### open
An "open" list can be created with

scala```
  INSERT INTO foo (id, flag) VALUES ${open(
    (1, "a"),
    (2, "b"),
    (3, "c")
  )}
```

this generates:

sql```
  INSERT INTO foo (id, flag) VALUES
    (?, ?),
    (?, ?),
    (?, ?)
```

The ability to produce constants from arbitrarily long lists is probably JDBC Interop's most useful feature.
Normally, prepared statements require knowing the length of lists ahead of time.

sql```SELECT * FROM foo WHERE id IN (?,?,?,?,?)```

JDBC Interop dynamically creates list constants with `?`s for each item, then sets each ? based on the associated item's type.

The user code:

scala```
    val activeStatus = false
    val flags = Seq(("a","b","c","b","e"))

    val query = sql"""
        SELECT * FROM foo WHERE flag IN ${SQLList(flags, abortIfEmpty=true)}
        AND active = ${activeStatus}"""
```

effectively becomes:

scala```
    val ps: PrepatedStatement = conn.prepare("SELECT * FROM foo WHERE id IN (?,?,?,?.?)\nAND active = ?")
    ps.setString(1, "a")
    ps.setString(2, "b")
    ps.setString(3, "c")
    ps.setString(4, "d")
    ps.setString(5, "e")
    ps.setBoolean(6, false)
```

### SQLList[A] Class
Instead of passing a tuple the `SQLList[A]` case class can be instantiated directly. This affords several config options:

scala```
  SQLList[A](
    values: Seq[A]                  // the source seq, required
    abortIfEmpty: Boolean,          // See [target]
    open: Boolean = false,          // if true enclosing parens omitted
    maxLength: Option[Int] = None   // See [target]
    autoChunk: Boolean = false      // See [target]
  )
```

### Empty List Handling
In many contexts an empty list constant is a syntax error. `SQLList#abortIfEmpty` sort-circuits query execution
if an empty list is encountered. Exec methods like `execQuery()` mapping over a result set will return Nil while methods returing `Boolean`
will return false. This enables "safe" execution when creating a list from a sequence type that could be empty.

If `SQLList#abortIfEmpty` is false (the default), the query execution is attempted possibly resulting in an exception or
other unexpected behavior.

In general, its best practice for your code to guard against empty lists being passed. In the case of a WHERE IN,
returning Nil seems intuitive. But in most other situations another strategy is probably best. Validating user inputs
to ensure at least one value is given, for example.

### Maximum Length for Lists
Some databases impose a limit on the size of list constants (1000 for Oracle 11g). By setting the `SQLList#maxLength`
property you may specify that an exception is thrown if a list of greater than `maxLength` would be created.

If `SQLList#autoChunk` is true, queries with lists longer than `maxLength` will be split into chunks. This feature is only
supported for queries where a single list of greater than `SQLList#maxLength` exits. It is encumbenet on the user to ensure
the rest of the query works the same in spite of chunkification.

As with empty lists, lists that exceed a certain bounds should in most cases be addressed by user code rather than rely
on JDBC interop to automagically fix problems. For long where in lists where the rest of the query is static they can prove handy.

### SQL Array
Because of the way variables are passed through `StringContext`, I was unable to overcome type erasure even using `TypeTags`.
Therefore sequences types are not automatically treated as arrays. An intance of SQLArray[A], which reliably obtains the type of
type argument A, is required. To define via the DSL use use the `array()` method:

scala```
  UPDATE foo SET arr = ${array("X", "Y", "Z") WHERE ...} // ensures an array of the correct type is created (`text[]` for PostgreSQL)
 ```

If you wish to create an array from a sequence type use the SQLArray[A] class.

scala```val arr = SQLArray(Seq("a","b","c"))```

## UNSAFE Methods and Classes
The DSL offers an escape hatch where regular string interpolation needs to be achived. These methods and classes are "unsafe".
The values they produce are inserted as is with no sanitization into the query. The must not be given data from user inputs
as this create an SQL injection vector.

### UNSAFE_direct
The UNSAFE_direct DSL method allows a variable to be inserted into the SQL string as is.

scala```
  val table = if (isDev) "dev_table" else "prod_table"
  val q = sql"SELECT * FROM ${UNSAFE_direct(table)} WHERE ..."
```

The text of `q` will be (assuming isDev is true)

sql```SELECT * FROM dev_table WHERE ...```

### UNSAFE_relation
Same as `UNSAFE_direct` except the string is double-quoted.

scala```
  val table = if (isDev) "dev_table" else "prod_table"
  val q = sql"SELECT * FROM ${UNSAFE_direct(table)} WHERE ..."
```

becomes

sql```SELECT * FROM "dev_table" WHERE ...```

As with `UNSAFE_direct`, `UNSAFE_relation` does not sanatize the input and must never be called with user input text/data.
JDBC does not provide a safe way to set column or other relation names dynamically.

## Setter
The `Setter` class provides a direct access to the `PSWrapper` and underlying prepared statement so that a value can be
set directly. This is useful when dealing with types not supported by JDBC interop.

Here we translate a boolean into Y/N to demonstrate:

scala```
  val isActive = false
  val query = sql"""UPDATE foo SET is_active = ${Setter((psw, idx)=>{
    psw.setString(idx, if (isActive) "Y" else "N")
  )}"""
  ...
```

The `Setter` constructor takes a "op" single argument. A function that takes the `PSWrapper` and the `Int` index of the value.
It is import that one and only one set method gets called otherwise query execution will fail. In other words, `Setter`
cannot be used to conditionally avoid setting a value.

## Options and null
Options are treated intuitively.

scala```
  val maybeString = Some("foo")
  val ps = conn.prepare(...)
  val idx = ...
  if (maybeString.isDefined) ps.setString(idx, maybeString.get)
  else ps.setNull(idx, java.sql.Types.NULL)
```

When updating or inserting, responsibility rests with the user to ensure Options are only passed where column is nullable.

If the JDBC Interop type descriminator encounters a naked null `setNull()` is called. This may change in future and an exception
will the thrown instead: We are in Scala land where one expects Options any time a value could be nonexistant.
Furthermore unboxed JVM value types cannot be null whereas columns of any type, even numeric types, can be nullable in typical RDBMS.
Treating None as null provides a consistent way to encode nullable value types.

## Json
Variables of type `play.api.libs.json.JsValue` can be passed where JSON is expected.

Thus

scala```
  val data = Json.obj("foo" -> 42).toString
  sql"""UPDATE raw_data SET json = data::json""" // use setString() and cast
```

becomes

scala```
  val data = Json.obj("foo" -> 42)
  sql"""UPDATE raw_data SET json = $data""" // JsObject passed directly, no cast
```

Currently PostgreSQL is the only database where automatic JSON handeling is effected. If `JsValue` is passed to an
unsupported database an exception is thrown.

## Supported Types
Apart from special types provided by JDBC Interop, the following well-known types are supported:

JVM Type      | Set With                       | Notes
 ---          | ---                            | ---
String        | setString                      |
Boolean       | setBoolean                     |
Int           | setInt                         |
Long          | setLong                        |
Double        | setDouble                      |
java.sql.Date | setDate                        |
Option[T]     | [depends on T]                 | See [Options and null]
JsValue       | setObject                      | [only supported by PostgreSQL]






