package effects

import scala.util.Random


object procedureal {

  def askForName(): String = {
    println("What is your name?")
    scala.io.StdIn.readLine
  }

  val random = new Random()

  def getLuckyNumber(name: String): Int = random.nextInt(10) + 1

  def main(args: Array[String]): Unit = {
    val name = askForName()
    val luckyNumber = getLuckyNumber(name)
    println(s"Hi $name, your lucky number is $luckyNumber")
  }
}