package utils

import java.io.Serializable

import shapeless.tag
import shapeless.tag.{@@, Tagged}

object RandomTagging extends App {

  sealed trait Event extends Product with Serializable

  object Event {

    final case class Created(i: Int) extends Event
    final case class Updated(i: Int) extends Event
    final case class Deleted(i: Int) extends Event
  }

  final case class DifferentDeleted(i: Int, difference: String)


  // this method is in library

  class Counter[A](val count: Int = 0) {
    def add(a: A): Counter[A] = new Counter(count + 1)
  }


  // how to define method which takes Created or Updated or DifferentDeleted ?

  // lets create tag
  trait CanBeCountedTag

  // so with this we can write count[TagCanBeCounted]
  // but how to tag only specific classes if we can't change them?
  // we can write TC instances for them

  class CanBeCountedTC[A]

  import Event._

  implicit val createdTC: CanBeCountedTC[Created] = new CanBeCountedTC()
  implicit val updatedTC: CanBeCountedTC[Updated] = new CanBeCountedTC()
  implicit val differentDeletedTC: CanBeCountedTC[DifferentDeleted] = new CanBeCountedTC()

  implicit def selectedEvents[A: CanBeCountedTC](a: A) = tag[CanBeCountedTag](a)

  val counter = new Counter[Tagged[CanBeCountedTag]]
  println(
    counter
      .add(Created(1))
      .add(Updated(1))
      .add(DifferentDeleted(1, "random"))
//      we can not att this because we do not have TC for Deleted
//      .add(Deleted(1))
      .count
  )

}
