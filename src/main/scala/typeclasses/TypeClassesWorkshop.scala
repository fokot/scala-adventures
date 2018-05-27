package typeclasses

object TypeClassesWorkshop {

  sealed trait JsonValue
  case class JsonObject(entries: Map[String, JsonValue]) extends JsonValue
  case class JsonArray(entries: Seq[JsonValue]) extends JsonValue
  case class JsonString(value: String) extends JsonValue
  case class JsonNumber(value: Double) extends JsonValue
  case class JsonBoolean(value: Boolean) extends JsonValue
  object JsonNull extends JsonValue


  object JsonWriter {
    def write(jsonValue: JsonValue): String = jsonValue match {
      case JsonObject(entries) => s"{${entries.map{case (key, value) => s""""$key": ${write(value)}"""}.mkString(", ")}}"
      case JsonArray(entries) => s"[${entries.map(write).mkString(",")}]"
      case JsonString(value) => s""""$value""""
      case JsonNumber(value) => value.toString
      case JsonBoolean(value) => value.toString
      case JsonNull => "null"
    }

    def write[A: Json](a: A): String =
      write(Json[A].asJson(a))
  }

  trait Json[A] {
    def asJson(a: A): JsonValue
  }

  object Json {

    def apply[A](implicit instance: Json[A]): Json[A] = instance

    implicit class Ops[A: Json](a: A) {
      def asJson: JsonValue = Json[A].asJson(a)
    }

    def instance[A](f: A => JsonValue): Json[A] = new Json[A] {
      override def asJson(a: A): JsonValue = f(a)
    }

    implicit val stringJson = Json.instance[String](a => JsonString(a))

    implicit val IntJson = Json.instance[Int](a => JsonNumber(a))

    implicit def optionJson[A: Json] = Json.instance[Option[A]] {
      case Some(a) => a.asJson
      case None => JsonNull
    }

    implicit def listJson[A: Json] = Json.instance[List[A]](as => JsonArray(as.map(_.asJson)))

  }

  case class User(firstName: String, age: Int)

  def main(args: Array[String]): Unit = {

    import Json._


    implicit val userJson = new Json[User] {
      override def asJson(a: User): JsonValue = JsonObject(
        Map(
          "firstName" -> a.firstName.asJson,
          "age" -> a.age.asJson
        )
      )
    }

    val someString = "Do. Or do not. There is no try."
    println(JsonWriter.write(implicitly[Json[String]].asJson(someString)))

    val someUser = User("Peter", 25)
    println(JsonWriter.write(someUser))
    println(JsonWriter.write(someUser.asJson))

    reversedString

  }

  def reversedString = {
    // new  scope so implicits don't collide

    import Json.Ops

    implicit def userJson(implicit stringJson: Json[String]) = new Json[User] {
      override def asJson(a: User): JsonValue = JsonObject(
        Map(
          "firstName" -> a.firstName.asJson,
          "age" -> a.age.asJson
        )
      )
    }

    val someUser = User("Peter", 25)
    implicit val stringReverseJson = Json.instance[String](a => JsonString(a.reverse))

    println(JsonWriter.write(someUser))
  }

  def defaulNotSerialisableJson = {
    // new  scope so implicits don't collide

    import Json.Ops

    implicit def userJson(implicit stringJson: Json[String]) = new Json[User] {
      override def asJson(a: User): JsonValue = JsonObject(
        Map(
          "firstName" -> a.firstName.asJson,
          "age" -> a.age.asJson
        )
      )
    }

    val someUser = User("Peter", 25)
    implicit val stringReverseJson = Json.instance[String](a => JsonString(a.reverse))

    println(JsonWriter.write(someUser))
  }
}
