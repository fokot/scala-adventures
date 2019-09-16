package implicitConversions

import scala.language.postfixOps

object ImplicitsMain {

  implicit  def intToPimpedInt(i : Int) = new PimpedInt(i)

  def main(args: Array[String]) {

    3 times println("aaa")

    println(5 minutes)

  }
}

class PimpedInt(i : Int){
  def times(f : => Unit) : Unit = for(x <- 0 until i) f
  def seconds = 1000 * i
  def minutes = 60 * seconds
}
