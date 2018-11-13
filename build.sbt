name := "scala-adventures"

version := "1.0"

scalaVersion := "2.12.7"

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.3.2",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.5",
  "org.typelevel" %% "cats" % "0.8.1",
  "com.twitter" %% "finagle-http" % "6.43.0",
  "com.slamdata" %% "matryoshka-core" % "0.18.3",
  "org.scalaz" %% "scalaz-core" % "7.2.9",
  "org.typelevel" %% "cats-effect" % "0.4"
)