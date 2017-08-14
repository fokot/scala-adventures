package free.monads.own

import scala.io.{Source, StdIn}

/** Idea is taken this blof post http://degoes.net/articles/easy-monads */
object consoleProgram extends App {

  import freemonad._
  import console._
  import http._

  // othwerwise implicit interpreter is not found
  console.interpreter
  http.interpreter


  println("\nconsole program start\n")

  // Sequential[ConsoleF, String]
  def consoleProgram = for {
    _ <- WriteLine("Enter your name").effect
    name <- ReadLine().effect
    a <- WriteLine(s"Your name is $name").effect
  } yield name

  interpret(consoleProgram)


  println("\nhttp program start\n")

  val httpProgram = HttpGet("https://status.github.com/api/status.json").effect
  val res = interpret(httpProgram)
  println(s"result from http is $res")


  println("\nconsole and http program start\n")

  type ConsoleAndHttpEither[A] = ({ type x[A] = EitherOr[ConsoleF, HttpF, A] })#x[A]

//  implicit val eitherOrInterpreter: Interpreter[ConsoleAndHttpEither] = new EitherOrInterpreter[ConsoleF, HttpF]()

  type ConsoleAndHttpIO[A] = Sequential[ConsoleAndHttpEither, A]

  // Sequential[ConsoleAndHttpEither, String]
  def consoleAndHttpProgram = for {
    // these crazy tye annotations are needed for scala
    _ <- Effect(IsLeft(WriteLine("Getting github status")): ConsoleAndHttpEither[Unit])
    status <- Effect(IsRight(HttpGet("https://status.github.com/api/status.json")): ConsoleAndHttpEither[String])
    _ <- Effect(IsLeft(WriteLine(s"Github status is $status")):  ConsoleAndHttpEither[Unit])
  } yield status

  interpret(consoleAndHttpProgram)

  println("\nprogram end")
}


object freemonad {

  sealed trait Sequential[F[_], A] {
    def map[B](f: A => B): Sequential[F, B] = Map[F, A, B](this, f)
    def flatMap[B](f: A => Sequential[F, B]): Sequential[F, B] = Chain[F, A, B](this, f)
  }

  case class Effect[F[_], A](a: F[A]) extends Sequential[F, A]
  case class Pure[F[_], A](a: A) extends Sequential[F, A]
  case class Chain[F[_], A, B](a: Sequential[F, A], f: A => Sequential[F, B]) extends Sequential[F, B]
  case class Map[F[_], A, B](a: Sequential[F, A], f: A => B) extends Sequential[F, B]

  trait Interpreter[F[_]] {
    def interpret[A](effect: F[A]): A
  }

  // anything with Interpreter can be interpreted
  def interpret[F[_] : Interpreter, A](seq: Sequential[F, A]): A = seq match {
    case Effect(effect) => implicitly[Interpreter[F]].interpret(effect)
    case Pure(a) => a
    case Chain(a, f) => interpret(f(interpret(a)))
    case Map(a, f) => f(interpret(a))
  }

  // anything with Interpreter can become Effect
  implicit def withInterpreterToEffect[F[_] : Interpreter, A](a: F[A]): {def effect: Effect[F, A]} = new {
    def effect = Effect(a)
  }

  sealed trait EitherOr[+F[_], +G[_], A]
  final case class IsLeft[F[_], G[_], A](left: F[A]) extends EitherOr[F, G, A]
  final case class IsRight[F[_], G[_], A](right: G[A]) extends EitherOr[F, G, A]


//  type Inject f g = forall a. PrismP (f a) (g a)
//  type Interpreter f g' = forall a g. Inject g g' -> f a -> Free g a


  class EitherOrInterpreter[F[_] : Interpreter, G[_] : Interpreter] extends Interpreter[({ type x[A] = EitherOr[F, G, A] })#x]{
    def interpret[A](effect: EitherOr[F, G, A]): A = effect match  {
      case IsLeft(effect) => implicitly[Interpreter[F]].interpret(effect)
      case IsRight(effect) => implicitly[Interpreter[G]].interpret(effect)
    }
  }

  // this does not work when i inline the interpreter to this function
  implicit def eitherOrInterpreter[F[_] : Interpreter, G[_] : Interpreter]: Interpreter[({ type x[A] = EitherOr[F, G, A] })#x] =
    new EitherOrInterpreter[F, G]()

}

object prism {
  case class PPrism[S, T, A, B](getOrModify: S => T Either A, reverseGet: B => T)
  type Prism[S, A] = PPrism[S, S, A, A]

  sealed trait IntOr[A]
  case class IsA[A](a: A) extends IntOr[A]
  case class IsInt[A](n: Int) extends IntOr[A]

  def prismIsA[A, B]: PPrism[IntOr[A], IntOr[B], A, B] =
    PPrism(
      (s: IntOr[A]) =>
        s match {
          case IsA(a) => Right(a)
          case IsInt(n) => Left(IsInt(n))
        }
      , IsA(_)
    )
}

object console {
  import freemonad.Interpreter

  sealed trait ConsoleF[A]
  case class WriteLine(line: String) extends ConsoleF[Unit]
  case class ReadLine() extends ConsoleF[String]

//  type ConsoleIO[A] = Sequential[ConsoleF, A]

  implicit val interpreter = new Interpreter[ConsoleF] {
    def interpret[A](effect: ConsoleF[A]): A = effect match  {
      case WriteLine(line) => println(line)
      case ReadLine() => StdIn.readLine()
    }
  }
}

object http {
  import freemonad.Interpreter

  sealed trait HttpF[A]
  case class HttpGet(line: String) extends HttpF[String]

  implicit val interpreter = new Interpreter[HttpF] {
    def interpret[A](effect: HttpF[A]): A = effect match  {
      case HttpGet(url) => Source.fromURL(url).getLines().mkString
    }
  }
}