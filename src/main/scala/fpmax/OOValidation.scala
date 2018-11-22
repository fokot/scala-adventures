package fpmax

trait Validator[A, B] {
  // how to decide if it fails fast or accumulates errors
  def rules: List[Rule[A, B]]
}

object Validator {
  def validateFailFast[A, B](a: A, v: Validator[A, B]): Either[String, A] = ???
  def validateCollectErrors[A, B](a: A, v: Validator[A, B]): Either[List[String], A] = ???
}

trait Rule[A, B] {
  def test(input: A): Either[String, B]
  // when we can't implement `message` why to have trait for single `test` function and not function itself?
  def message: String

  def combine[C](r: Rule[B, C]): Rule[A, C] = new Rule[A, C] {
    def test(input: A): Either[String, C] =
        Rule.this.test(input).flatMap(r.test)
    def message: String = ??? // how to implement this?
  }


}

object OOValidation extends App {

  class NonEmpty(name: String) extends Rule[Map[String, String], String] {
    override def test(input: Map[String, String]): Either[String, String] = ???

    override def message: String = ???
  }

  class IsNumber(n: String) extends Rule[String, Int] {
    override def test(input: String): Either[String, Int] = ???

    override def message: String = ???
  }


  val formData: Map[String, String] = ???

  case class Address(street: String, number: Int)




}
