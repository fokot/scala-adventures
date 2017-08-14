package onlyvalid

import scala.util.Try

object OnlyValid {


  trait Validated[+A]
  case class Valid[A](a: A) extends Validated[A]
  case object Invalid extends Validated[Nothing]


  case class User private (name: String, age: Int)


  def main(args: Array[String]): Unit = {
    Try {
      5
    }.failed.foreach(e => print(e))
  }

}


class Other {

  import OnlyValid._

//  val constructor = new User("ss", 5)

//  val applyFromCompanion = User("ss", 5)

}