name := "jdbcinterop"
scalaVersion := "2.12.2"

// For ScalaTest
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

libraryDependencies += "com.h2database" % "h2" % "1.4.199" % Test

// ScalaTest requires `resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases" in  ~/.sbt/0.13/global.sbt`
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

// https://mvnrepository.com/artifact/org.postgresql/postgresql
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.5"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.7.2"

// set to `false` to run test in series
parallelExecution in Test := true