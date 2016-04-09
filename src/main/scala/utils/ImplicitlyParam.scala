package utils

object ImplicitlyParam extends App {

  // the same as implicitly
  def implicitlyParam[A](implicit a: A) = a

  implicit val x = "I'm implicit value"

  implicitlyParam[String].foreach(println)
}