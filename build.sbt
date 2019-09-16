name := "scala-adventures"

version := "1.0"

scalaVersion := "2.13.0"

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.3.3",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
  "org.typelevel" %% "cats-core" % "2.0.0",
  "org.typelevel" %% "cats-free" % "2.0.0",
  "org.typelevel" %% "cats-effect" % "2.0.0",
  "org.scalaz" %% "scalaz-core" % "7.2.28",
  "dev.zio" %% "zio" % "1.0.0-RC12-1"
)
