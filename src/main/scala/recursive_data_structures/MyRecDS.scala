package recursive_data_structures

import scala.language.higherKinds

/**
  * Examples taken from http://akmetiuk.com/posts/2017-03-10-matryoshka-intro.html
  *
  * A fixed point of a function f(_) is a value x such that
  * f(x) == x.
  * We can think of a higher-order function fix that computes a fixed point of its argument:
  * fix(f) == x such that f(x) == x.
  * By extension, fix(f) == f(fix(f)) holds.
  */

trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

object Functor {

  implicit class FunctorOps[F[_]: Functor, A](fa: F[A]) {
    def map[B](f: A => B) = implicitly[Functor[F]].map(fa)(f)
  }
}

// Fixed point type
case class Fix[F[_]](unfix: F[Fix[F]])

object catamorphism {

  import Functor._

  // Catamorphism
  def cata[F[_]: Functor, A](structure: Fix[F])(algebra: F[A] => A): A =
    algebra(structure.unfix.map(cata(_)(algebra)))
}

// Expr
sealed trait Expr[+A]
case class Add [A](expr1: A, expr2: A) extends Expr[A]
case class Mult[A](expr1: A, expr2: A) extends Expr[A]
case class Num(literal: Int) extends Expr[Nothing]

object Expr {

  implicit val exprFunctor = new Functor[Expr] {
    override def map[A, B](fa: Expr[A])(f: A => B): Expr[B] = fa match {
      case Add(expr1, expr2) => Add(f(expr1), f(expr2))
      case Mult(expr1, expr2) => Mult(f(expr1), f(expr2))
      case n @ Num(_) => n
    }
  }
}

object MyRecDS extends App {

  import catamorphism._

  def eval(e: Fix[Expr]): Int = cata[Expr, Int](e) {
    case Add(expr1, expr2) => expr1 + expr2
    case Mult(expr1, expr2) => expr1 * expr2
    case Num(n) => n
  }

  val expr: Fix[Expr] =
    Fix(Add(
      Fix(Mult(
        Fix[Expr](Num(4)),
        Fix[Expr](Num(3))
      )),
      Fix[Expr](Num(3))
    ))
  val exprRes = eval(expr)
  println(exprRes)  // 15

}

