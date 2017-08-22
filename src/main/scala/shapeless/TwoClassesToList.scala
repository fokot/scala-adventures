package shapeless

import shapeless._
import shapeless.labelled.{FieldType, field}
import shapeless.ops.hlist.{Align, ToTraversable, ZipWith}

object TwoClassesToList {

  // HLIST
  def hlistZipArgsToList[A, B, P <: Poly2, ARepr <: HList, BRepr <: HList, R <: HList]
  (a: A, b: B, f: P)(
     implicit
     aGen : Generic.Aux[A, ARepr],
     bGen : Generic.Aux[B, BRepr],
     zipWith : ZipWith.Aux[ARepr, BRepr, P, R],
     toTrav: ToTraversable.Aux[R, List, String]
   ): List[String] =
      aGen.to(a).zipWith(bGen.to(b))(f).toList


  object hlistSum extends Poly2 {
    implicit def repr[A]: Case.Aux[A, A, String] = at[A, A]( (a, b) => (a, b).toString)
  }

  // RECORD
  def recordZipArgsToList[A, B, P <: Poly2, ARepr <: HList, BRepr <: HList, R <: HList]
  (a: A, b: B, f: P)(
     implicit
     aGen : LabelledGeneric.Aux[A, ARepr],
     bGen : LabelledGeneric.Aux[B, BRepr],
     align : Align[ARepr, BRepr],
     zipWith : ZipWith.Aux[BRepr, BRepr, P, R],
     toTrav: ToTraversable.Aux[R, List, String]
   ): List[String] =
      align.apply(aGen.to(a)).zipWith(bGen.to(b))(f).toList


  object recordSum extends Poly2 {
    implicit def repr[K <: Symbol, V] = at[FieldType[K, V], FieldType[K, V]] { (a, b) =>
      field[K]( (a, b).toString )
    }
  }

  def main(args: Array[String]): Unit = {
    case class Data(id: Long, s2: String, s: String)
    case class DataFilter(id: Long, s: String)
    case class DataFilter2(id: Long, s: String, s2: String)

    val d = Data(5, "first", "nothing")
    val f = DataFilter(30, "second")
    val f2 = DataFilter2(30, "xxx", "third")

    val v = hlistZipArgsToList(d, f, hlistSum)
    println(v)

//    val w = recordZipArgsToList(d, f, recordSum)
//    println(w)

    val x = recordZipArgsToList(d, f2, recordSum)
    println(x)
  }
}