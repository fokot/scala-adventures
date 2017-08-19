package shapeless

import shapeless._, labelled._, ops.hlist._

object Update extends Poly2 {
  implicit def all[A, B, R1 <: HList, R2 <: HList](
    implicit
    oldGen: LabelledGeneric.Aux[A, R1],
    newGen: LabelledGeneric.Aux[B, R2],
    z: ZipWith.Aux[R1, R2, Update.type, R1]) =
      at[A, B] { (old, maybeNew) =>
        val oldRepr = oldGen.to(old)
        val newRepr = newGen.to(maybeNew)
        oldGen.from(oldRepr.zipWith(newRepr)(Update))
      }
  implicit def repr[K <: Symbol, V] =
    at[FieldType[K, V], FieldType[K, Option[V]]] { (old, maybeNew) =>
      field[K](maybeNew getOrElse old)
    }
}

object UpdateExample extends App{
  case class B(a: Int, b: Int, c: String)
  case class Diff(a: Option[Int] = None, b: Option[Int] = None, c: Option[String] = None)

  val a = Update(B(1, 3, "xx"), Diff(Some(2), c = Some("NEW")))
  println(a)
  // res0: Example.B = B(2,3)
  // compile time error if the formats are not compatible for field names or types
}
