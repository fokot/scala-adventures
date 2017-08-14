package parser



// Functor, Applicative, Monad hierarchy

trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

trait Applicative[F[_]] extends Functor[F] {
  def pure[A](a: A): F[A]
  def apply[A, B](fa: F[A])(f: F[A => B]): F[B]

  override def map[A, B](fa: F[A])(f: A => B): F[B] = apply(fa)(pure(f))
}

trait Monad[F[_]] extends Applicative[F] {
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

  override def apply[A, B](fa: F[A])(f: F[A => B]): F[B] =
    flatMap(f)(map(fa) _)
}

// aritmetics AST
sealed trait Expr

case class Number(n: Int) extends Expr
case class Add(lhs: Expr, rhs: Expr) extends Expr
case class Minus(lhs: Expr, rhs: Expr) extends Expr
case class Mult(lhs: Expr, rhs: Expr) extends Expr
case class Div(lhs: Expr, rhs: Expr) extends Expr


// SQL AST
//sealed trait SQL
//
//case class Select(columns: List[ColumnName], from: TableName, where: Option[Where])
//
//case class TableName(value: String, alias: Option[String]) extends SQL
//case class ColumnName(value: String) extends SQL
//
//sealed trait Operator extends SQL
//object `=` extends Operator
//object `<>` extends Operator
//object `>` extends Operator
//object `<` extends Operator
//object `in` extends Operator


// parser
//type Parser s t = [s] -> [(t,[s])]
case class Parser[A](f: String => List[(A, String)])

//object ApplicativeParser {
//
//  def pSym[A](x: String): Parser[String] = Parser {
//    s => if(s.startsWith(x)) List((x, s.substring(x.length))) else null
//  }
//
//  def pSym[A](x: String): Parser[String] = Parser {
//
//  }
//
//  pReturn :: t -> Parser s t
//  pReturn a ss = [(a,ss)]
//
//}



//pSym :: Eq s => s -> Parser s s
//pSym a [] = []
//pSym a (s:ss) = if s == a then [(s,ss)]
//else []




object MyParser extends App {
}
