package typeclasses

object TypeClassApp extends App {

  implicit def addJson[A](a : A)(implicit json: Json[A]) = new {
    def jsonString = JsonWriter write (json.json(a))
  }
  implicit def addExpression[A](a : A)(implicit exp: Expression[A]) = new { def value = exp.value(a) }

  implicit object NumberExpression extends Expression[Number] {
    def value(n: Number) = n.value
  }

  implicit def plusExpression[A : Expression, B : Expression] =
    new Expression[Plus[A, B]] {
      def value(p: Plus[A, B]) = implicitly[Expression[A]].value(p.lhs) +
        implicitly[Expression[B]].value(p.rhs)
    }

  implicit def minusExpression[A : Expression, B : Expression] =
    new Expression[Minus[A, B]] {
      def value(p: Minus[A, B]) = implicitly[Expression[A]].value(p.lhs) -
        implicitly2[Expression[B]].value(p.rhs)
    }

  def implicitly2[T] (implicit t: T) = t

  Minus(Number(5), Number(3)).value

  implicit object NumberJson extends Json[Number] {
    def json(n: Number) = JsonNumber(n.value)
  }


  implicit def plusJson[A : Json, B : Json] =
    new Json[Plus[A, B]] {
      def json(p: Plus[A, B]) = JsonObject(
        Map("op" -> JsonString("+"),
          "lhs" -> implicitly[Json[A]].json(p.lhs),
          "rhs" -> implicitly[Json[B]].json(p.rhs)
        ))
    }

  implicit def minusJson[A : Json, B : Json] =
    new Json[Minus[A, B]] {
      def json(p: Minus[A, B]) = JsonObject(
        Map("op" -> JsonString("-"),
          "lhs" -> implicitly[Json[A]].json(p.lhs),
          "rhs" -> implicitly[Json[B]].json(p.rhs)
        ))
    }

  println("value is : " + Plus(Number(2), Minus(Number(3), Number(2))).value )

  implicit object StringJson extends Json[String] {
    override def json(value: String) = JsonString(value)
  }

  //  {
  //    implicit object NoStringsJson extends Json[String] {
  //      override def json(value: String) = JsonString("NOS STRINGS IN JSON!!!")
  //    }

  println("json is : " + Plus(Number(2), "sadfasd").jsonString)
  println("json is : " +  JsonWriter.write(implicitly[Json[Plus[Number, String]]].json(Plus(Number(2), "sadfasd"))))
  //  }


  //  implicit object StringJson extends Json[String] {
  //    override def json(value: String) = JsonString(value)
  //  }
  //
  //  println("json is : " + Plus(Number(2), Minus(Number(3), "abc")).jsonString )
  //
  //  implicit def simplePairToJson[A: Json] = {
  //    new Json[(A, A)] {
  //      override def json(value: (A, A)) = JsonObject(Map(
  //        "simple.1" -> implicitly[Json[A]].json(value._1),
  //        "simple.2" -> implicitly[Json[A]].json(value._2)
  //      ))
  //    }
  //  }
  //
  //  implicit def pairToJson[A: Json, B: Json] = {
  //    new Json[(A, B)] {
  //      override def json(value: (A, B)) = JsonObject(Map(
  //        "1" -> implicitly[Json[A]].json(value._1),
  //        "2" -> implicitly[Json[B]].json(value._2)
  //      ))
  //    }
  //  }
  //
  //  {
  //    implicit def f = simplePairToJson
  //    println("json is : " + Plus(Number(2), Minus(Number(3), ("one", "two"))).jsonString )
  //  }





  //  implicit object StringJson extends Json[String] {
  //    def json(value: String) = JsonString(value)
  //  }
  //
  //  implicit def pairToExpression[A: Expression, B: Expression] = {
  //    new Expression[(A, B)] {
  //      def value(pp: (A, B)) = pp._1.value * pp._2.value
  //    }
  //  }
  //
  //
  //  implicit object StringPairJson extends Json[(String, String)] {
  //    def json(value: (String, String)) = JsonString("This is a string pair")
  //  }
  //
  //  implicit object IntJson extends Json[Int] {
  //    def json(value: Int) = JsonString(value.toString)
  //  }
  //
  //  implicit def pairToJson[A: Json, B: Json] = {
  //    new Json[(A, B)] {
  //      def json(pp: (A, B)) = JsonObject(Map("1" -> implicitly[Json[A]].json(pp._1), "2" -> implicitly[Json[B]].json(pp._2)))
  //    }
  //  }
  //
  //  println((Number(2), Number(2)).jsonString)
  //  println((Number(2), "asdfasdf").jsonString)
  //  println((Number(2), 5).jsonString)
  //
  //
  //  // pair also works as we created a typeclass for it
  //  println("pair value is : " + (Plus(Number(2), Number(3)), Number(3)).value )
  //  println("pair json is : " + (Plus(Number(2), Number(3)), Number(3)).jsonString )
  //
  //  // ambigious
  ////  println("string pair json is : " + ("aa", "bb").jsonString )
  //  println("string pair json is : " + addJson("aa", "bb")(pairToJson).jsonString )
  //  println("string pair json is : " + addJson("aa", "bb")(StringPairJson).jsonString )

}

