package fpmax

import scala.io.StdIn.readLine
import scala.util.Try

object AppMethods {

  def checkContinue(name: String): Boolean = {
    println("Do you want to continue, " + name + "?")
    val input = readLine().toLowerCase
    input match {
        case "y" => true
        case "n" => false
        case _   => checkContinue(name)
      }
    }

  def printResults(input: String, num: Int, name: String): Unit =
    Try(input.toInt).toOption.fold(
      println("You did not enter a number")
    )(
      guess =>
        if (guess == num) println("You guessed right, " + name + "!")
        else println("You guessed wrong, " + name + "! The number was: " + num)
    )

  def gameLoop(name: String): Unit = {
    val num = scala.util.Random.nextInt(5) + 1

    println("Dear " + name + ", please guess a number from 1 to 5:")

    val guess = readLine()

    printResults(guess, num, name)

    if(checkContinue(name))
      gameLoop(name)
  }

  def main(args: Array[String]): Unit = {
    println("What is your name?")

    val name = readLine()

    println("Hello, " + name + ", welcome to the game!")

    gameLoop(name)
  }
}
