// matryoshka not yet in scala 2.13 :(

/*
package recursive_data_structures

import matryoshka.{ Recursive, Corecursive }
import matryoshka.data.Fix  // The fixed-point type similar to the one we implemented ad-hoc, but with Recursive and Corecursive instances.
import matryoshka.implicits._  // Syntax

object MatryoshkaExpr extends App {

  implicit val exprScalazFunctor = new scalaz.Functor[Expr] {
    override def map[A, B](fa: Expr[A])(f: (A) => B): Expr[B] = fa match {
      case Add(expr1, expr2) => Add(f(expr1), f(expr2))
      case Mult(expr1, expr2) => Mult(f(expr1), f(expr2))
      case n @ Num(_) => n
    }
  };

  // Evaluate an expression
  def eval[T](e: T)(implicit T: Recursive.Aux[T, Expr]): Int = e.cata[Int] {
    case Add (x1, x2) => x1 + x2
    case Mult(x1, x2) => x1 * x2
    case Num (x)      => x
  }

  def expr[T](implicit T: Corecursive.Aux[T, Expr]): T =
    Add(
      Mult(
        Num(2).embed,
        Num(3).embed
      ).embed,
      Num(5).embed
    ).embed
  val exprRes = eval(expr[matryoshka.data.Fix[Expr]])
  println(exprRes)  // 11

}

 */
