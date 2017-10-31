package io

import cats.effect.IO
import cats.implicits.catsStdInstancesForList
import cats.syntax.traverse._

object CatsIOList extends App {

  type A = Either[Throwable, String]

  val ios: List[IO[String]] = List(
    IO.apply("result one"),
    IO.raiseError(new Exception("aaa")),
    IO.raiseError(new Exception("bbb")),
    IO.apply("result two")
  )

  val iosAttempt: List[IO[A]] = ios.map(_.attempt)

  val res: IO[List[A]] = iosAttempt.sequence

  val par: IO[(List[A], List[A])] = res.map(_.partition(_.isRight))

  val (successes, failures) = par.unsafeRunSync()

  println(s"successes: $successes")
  println(s"failures: $failures")
}
