package shapeless

import cats.Monoid
import cats.instances.all._

import shapeless.labelled.{field, FieldType}
import shapeless.ops.hlist



object MigrationsApp extends App {

  trait Migration[A, B] {
    def apply(a: A): B
  }

  implicit class MigrationOps[A](a: A) {
    def migrateTo[B](implicit migration: Migration[A, B]): B =
      migration.apply(a)
  }

  def createMonoid[A](zero: A)(add: (A, A) => A): Monoid[A] =
    new Monoid[A] {
      def empty = zero
      def combine(x: A, y: A): A = add(x, y)
    }

  implicit val hnilMonoid: Monoid[HNil] = createMonoid[HNil](HNil)((x, y) => HNil)

  implicit def emptyHList[K <: Symbol, H, T <: HList](
     implicit
     hMonoid: Lazy[Monoid[H]],
     tMonoid: Monoid[T]
   ): Monoid[FieldType[K, H] :: T] =
    createMonoid(field[K](hMonoid.value.empty) :: tMonoid.empty) {
      (x, y) =>
        field[K](hMonoid.value.combine(x.head, y.head)) ::
          tMonoid.combine(x.tail, y.tail)
    }

  implicit def genericMigration[
  A, B, ARepr <: HList, BRepr <: HList,
  Common <: HList, Added <: HList, Unaligned <: HList
  ](
     implicit
     aGen : LabelledGeneric.Aux[A, ARepr],
     bGen : LabelledGeneric.Aux[B, BRepr],
     inter : hlist.Intersection.Aux[ARepr, BRepr, Common],
     diff : hlist.Diff.Aux[BRepr, Common, Added],
     monoid : Monoid[Added],
     prepend : hlist.Prepend.Aux[Added, Common, Unaligned],
     align : hlist.Align[Unaligned, BRepr]
   ): Migration[A, B] =
    new Migration[A, B] {
      def apply(a: A): B =
        bGen.from(align(prepend(monoid.empty, inter(aGen.to(a)))))
    }



  case class IceCreamV1(name: String, numCherries: Int, inCone: Boolean)

  // Remove fields:
  case class IceCreamV2a(name: String, inCone: Boolean)

  // Reorder fields:
  case class IceCreamV2b(name: String, inCone: Boolean, numCherries: Int)

  // Insert fields (provided we can determine a default value):
  case class IceCreamV2c(name: String, inCone: Boolean, numCherries: Int, numWaffles: Int)

  val original = IceCreamV1("Sundae", 1, true)
  println(original)

  val ic2a = original.migrateTo[IceCreamV2a]
  println(ic2a)
  // res14: IceCreamV2a = IceCreamV2a(Sundae,true)

  val ic2b = original.migrateTo[IceCreamV2b]
  println(ic2b)
  // res15: IceCreamV2b = IceCreamV2b(Sundae,true,1)

  val ic2c = original.migrateTo[IceCreamV2c]
  println(ic2c)
  // res16: IceCreamV2c = IceCreamV2c(Sundae,true,1,0)

}
