package shapeless

import shapeless._
import shapeless.record._
import shapeless.ops.record.Selector

object GetField extends App {

  case class A(id: Int, a: String)
  case class B(b: Int, a: String, id: Int)
  case class C(b: Int, a: String)

  def idValue[A, H <: HList](a: A)(implicit
   gen: LabelledGeneric.Aux[A, H],
   selector: Selector.Aux[H, Witness.`'id`.T, Int]
  ): Int = gen.to(a).get('id)

  val a = A(1, "abc")
  val b = B(1, "abc", 3)
  val c = C(1, "abc")

  println(idValue(a))
  println(idValue(b))
//  println(idValue(c))
}
