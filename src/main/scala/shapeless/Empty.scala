package shapeless

trait Empty[A] {
  def empty: A
}

object Empty {

  def apply[A: Empty] = implicitly[Empty[A]]

  def instance[A](a: A) = new Empty[A] {
    override def empty = a
  }

  implicit val emptyInt = instance[Int](1)
  implicit val emptyString = instance[String]("")
  implicit def emptyOption[A] = instance[Option[A]](None)
  implicit val emptyHList = HNil

  implicit def addEmptyToHList[A: Empty, H <: HList](implicit h: H): A :: H = Empty[A].empty :: h
  // FIXME remove this
  implicit def addEmptyIntToHList[H <: HList](implicit h: H): Int :: H = Empty[Int].empty :: h
  implicit def emptyCaseClass[A, H <: HList](implicit gen: Generic.Aux[A, H], h: H) = instance[A](gen.from(h))

  def createEmpty[A: Empty] = Empty[A].empty
}

object EmptyTest extends App {
  import Empty._

  case class A()
  case class B(i: Int)
  case class C(b: Option[Int])
  case class D(b: Option[Int], a: Option[A])
  case class E(a: A, b: B, c: Option[C])

  println(createEmpty[A])
  println(createEmpty[Int])
  println(createEmpty[B])
//  println(createEmpty[C])
//  println(createEmpty[D]())
//  println(createEmpty[E]())
}
