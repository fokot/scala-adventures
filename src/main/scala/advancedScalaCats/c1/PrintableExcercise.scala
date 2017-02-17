package advancedScalaCats.c1

trait Printable[A] {

  def format(a: A): String
}

object PrintDefaults {

  implicit val stringPrintable = new Printable[String] {
    override def format(a: String) = a
  }

  implicit val intPrintable = new Printable[Int] {
    override def format(a: Int) = a.toString
  }
}

object Print {

  def format[A](a: A)(implicit printable: Printable[A]): String = printable.format(a)

  def print[A](a: A)(implicit printable: Printable[A]): Unit = println(format(a))
}


final case class Cat(name: String, age: Int, color: String)


object CatPrintable extends Printable[Cat] {
  import PrintDefaults._

  override def format(a: Cat) = s"${Print.format(a.name)} is a ${Print.format(a.age)} year-old ${Print.format(a.color)} cat."
}

object PrintableExcercise extends App {

  implicit val catPrintable = CatPrintable

  val cat = Cat("Micka", 3, "black")

  Print.print(cat)
}

