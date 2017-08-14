package shapeless

import shapeless._
import shapeless.record._

object Labels extends App {

  case class IceCream(name: String, numCherries: Int, inCone: Boolean)
  val sundae = LabelledGeneric[IceCream].
    to(IceCream("Sundae", 231, false))

  val x : Int = sundae.get('numCherries)

  println(x)

  val s = sundae.updated('numCherries, 45)
  println(LabelledGeneric[IceCream].from(s))
}
