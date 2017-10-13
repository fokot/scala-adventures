package shapeless


// I needed to use `kinda curried type parameter`
// https://tpolecat.github.io/2015/07/30/infer.html
final case class WithNones[A]() {
  def apply[H <: HList]()(implicit gen: Generic.Aux[A, H],  h: H): A = gen.from(h)
}

object WithNones {

  implicit val emptyHlist = HNil

  implicit def addNoneToHList[A, H <: HList](implicit h: H): Option[A] :: H = None :: h

  /**
    * If class has all fields Options it will create instance of class with all `None`
    *
    * Usage:
    *
    * case class A()
    * case class B(notAOption: Int)
    * case class C(b: Option[Int])
    * case class D(b: Option[Int], a: Option[A])
    *
    * println(createWithAllNones[A]())
    * // will not compile
    * // println(createWithAllNones[B])
    * println(createWithAllNones[C]())
    * println(createWithAllNones[D]())
    */

  def createWithAllNones[A] = new WithNones[A]
}