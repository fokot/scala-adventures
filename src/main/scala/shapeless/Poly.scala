package shapeless

import shapeless.MigrationsApp.IceCreamV1
import shapeless._
import shapeless.ops.hlist

object PolyApp extends App {

  trait ProductMapper[A, B, P] {
    def apply(a: A): B
  }

  implicit def genericProductMapper[
    A, B,
    P <: Poly,
    ARepr <: HList,
    BRepr <: HList
  ](
     implicit
     aGen: Generic.Aux[A, ARepr],
     bGen: Generic.Aux[B, BRepr],
     mapper: hlist.Mapper.Aux[P, ARepr, BRepr]
   ): ProductMapper[A, B, P] =
    new ProductMapper[A, B, P] {
      def apply(a: A): B =
        bGen.from(mapper.apply(aGen.to(a)))
    }

  implicit class ProductMapperOps[A](a: A) {
    class Builder[B] {
      def apply[P <: Poly](poly: P)
                          (implicit pm: ProductMapper[A, B, P]): B =
        pm.apply(a)
    }
    def mapTo[B]: Builder[B] = new Builder[B]
  }

  object conversions extends Poly1 {
    implicit val intCase: Case.Aux[Int, Boolean] = at(_ > 0)
    implicit val boolCase: Case.Aux[Boolean, Int] = at(if(_) 1 else 0)
    implicit val strCase: Case.Aux[String, String] = at(identity)
  }
  case class IceCream1(name: String, numCherries: Int, inCone: Boolean)
  case class IceCream2(name: String, hasCherries: Boolean, numCones: Int)

  val original = IceCreamV1("Sundae", 1, false)
  println(original)

  val ic2 = original.mapTo[IceCream2](conversions)
  println(ic2)
  // res2: IceCream2 = IceCream2(Sundae,true,0)

}
