package fpmax

import scala.io.StdIn.readLine
import scala.util.Try

object AppIO {

  case class IO[A](unsafeRun: () => A) { self =>
    def map[B](f: A => B): IO[B] = IO(() => f(self.unsafeRun()))

    def flatMap[B](f: A => IO[B]): IO[B] = IO(() => f(self.unsafeRun()).unsafeRun())
  }

  object IO {
    def point[A](a: => A): IO[A] = IO(() => a)
  }

  def parseInt(s: String): Option[Int] = Try(s.toInt).toOption
  def putStrLn(line: String): IO[Unit] = IO(() => println(line))
  def getStrLn: IO[String] = IO(() => readLine())
  def nextInt(upper: Int): IO[Int] = IO(() => util.Random.nextInt(upper))

  def checkContinue(name: String): IO[Boolean] =
    for {
      _     <- putStrLn("Do you want to continue, " + name + "?")
      input <- getStrLn.map(_.toLowerCase)
      cont  <- input match {
        case "y" => IO.point(true)
        case "n" => IO.point(false)
        case _   => checkContinue(name)
      }
    } yield cont

  def printResults(input: String, num: Int, name: String): IO[Unit] =
    parseInt(input).fold(
      putStrLn("You did not enter a number")
    )(guess =>
      if (guess == num) putStrLn("You guessed right, " + name + "!")
      else putStrLn("You guessed wrong, " + name + "! The number was: " + num)
    )

  def gameLoop(name: String): IO[Unit] =
    for {
      num   <- nextInt(5).map(_ + 1)
      _     <- putStrLn("Dear " + name + ", please guess a number from 1 to 5:")
      input <- getStrLn
      _     <- printResults(input, num, name)
      cont  <- checkContinue(name)
      _     <- if (cont) gameLoop(name) else IO.point(())
    } yield ()

  def mainIO: IO[Unit] =
    for {
      _     <- putStrLn("What is your name?")
      name  <- getStrLn
      _     <- putStrLn("Hello, " + name + ", welcome to the game!")
      _     <- gameLoop(name)
    } yield ()


  def main(args: Array[String]): Unit = {
    println("ioioioi")
    mainIO.unsafeRun()
  }
}
