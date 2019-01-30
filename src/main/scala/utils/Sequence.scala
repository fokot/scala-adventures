package utils

import cats.Applicative
import cats.effect.IO

object Sequence extends App {

  import cats.syntax.apply._
  def sequence[G[_]: Applicative, A](l: List[G[A]]): G[List[A]] =
    l.foldRight(Applicative[G].pure(List.empty[A]))(
      (a, as) => (a, as).mapN(_ :: _))


  def p(i: Int): IO[Int] = IO({println(i); i})

  val l = List(p(1), p(3), p(8))

  println(sequence(l).unsafeRunSync())
}
