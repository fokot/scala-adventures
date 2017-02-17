package applicative

import cats.data.ReaderT
import cats.instances.all._
import cats.{Applicative, Id}
import cats.syntax.cartesian._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object ApplicativeComposition extends App {

  implicit val timeout: FiniteDuration = 5 seconds

  def length: String => Future[Int] = x => Future.successful(x.length)
  def palindrome: String => Future[Boolean] = x => Future.successful(x == x.reverse)

  case class Stats(l: Int, p: Boolean)

  def getStats[F[_]: Applicative](l: String => F[Int], p: String => F[Boolean]): String => F[Stats] =
    x => Applicative[F].map2(l(x), p(x))(Stats)

  val res1 = getStats(length, palindrome)

  println(Await.result(res1("abba"), timeout))

  val res2 = getStats[Id](x => x.length, x => x == x.reverse)
  println(res2("scala"))

  def getStats2[F[_]: Applicative](l: ReaderT[F, String, Int], p: ReaderT[F, String, Boolean]): ReaderT[F, String, Stats] =
    Applicative[ ({type X[L] = ReaderT[F, String, L]})#X ].map2(l, p)(Stats)


  def length2: ReaderT[Future, String, Int] = ReaderT(x => Future.successful(x.length))
  def palindrome2: ReaderT[Future, String, Boolean] = ReaderT(x => Future.successful(x == x.reverse))

  val res3 = getStats2(length2, palindrome2)

  println(Await.result(res3("abba"), timeout))

  val res4 = getStats2[Id](ReaderT[Id, String, Int](x => x.length), ReaderT[Id, String, Boolean](x => x == x.reverse))
  println(res4("scala"))

  implicit def functionToReaderT[F[_], A, B](f: A => F[B]) = ReaderT[F, A, B](f)

//  implicit def functionToReaderTId[Id, A, B](f: A => B) = ReaderT[Id, A, B](f)
//  // type inference not working with functionToReaderT so also functionToReaderTId is needed
//  val res5 = getStats2[Id]((x: String) => x.length, (x: String) => x == x.reverse)
//  println(res5)

  val res6 = getStats2(length, palindrome)
  println(Await.result(res6("abba"), timeout))

  // with ats.syntax.cartesian._
  def getStats3[F[_]: Applicative](l: String => F[Int], p: String => F[Boolean]): String => F[Stats] =
    x => (l(x) |@| p(x)) map Stats

  val res7 = getStats2(length, palindrome)
  println(Await.result(res7("abba"), timeout))
}