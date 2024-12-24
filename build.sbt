val scala3Version = "3.6.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "de-jdp",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,
    libraryDependencies += "com.softwaremill.sttp.client4" %% "core" % "4.0.0-M20",
    libraryDependencies += "org.json4s" %% "json4s-native" % "4.0.7",
    libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "2.0.0"
  )
