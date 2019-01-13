package utils

import cats.implicits._
import cats.data.Kleisli

object SafeGetter  {

  type SafeGetter[A, B] = Kleisli[Option, A, B]

  def safeGetter[A, B](f: A => B): SafeGetter[A, B] =
    Kleisli(a => Option(f(a)))

  implicit class GetOps[A, B, C](sg: SafeGetter[A, B]) {

    private def lift(f: B => C): B => Option[C] =
      b => Option(f(b))

    def ? (f: B => C):  SafeGetter[A,  C] =
      sg andThen lift(f)
  }

}

object SafeGetterApp extends App {

  import utils.SafeGetter.SafeGetter
  import utils.SafeGetter._

  case class Token(content: String)
  case class ApiClientConfig(token: Token)
  case class JavaClientApi(config: ApiClientConfig)

  val tokenLength: SafeGetter[JavaClientApi, Int] =
    safeGetter[JavaClientApi, ApiClientConfig](_.config) ? (_.token) ? (_.content) ? (_.length)

  println(tokenLength(JavaClientApi(ApiClientConfig(Token("ttt")))))
  println(tokenLength(JavaClientApi(ApiClientConfig(Token(null)))))
  println(tokenLength(JavaClientApi(ApiClientConfig(null))))
  println(tokenLength(JavaClientApi(null)))

}



