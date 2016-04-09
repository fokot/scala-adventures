package typeclasses

trait Expression[A] {
  def value(expression: A): Int
}

case class Number(value: Int)
case class Plus[A, B](lhs: A, rhs: B)
case class Minus[A, B](lhs: A, rhs: B)