# Type classes workshop

* Watch this [video](https://www.youtube.com/watch?v=sVMES4RZF-8) to learn about type classes

* Write json AST and `JsonWriter` which takes json and serialises it to `String`
Replace `???` with code

```scala
sealed trait JsonValue
case class JsonObject(entries: Map[String, JsonValue]) extends JsonValue
case class JsonList(entries: Seq[JsonValue]) extends JsonValue
case class JsonString(value: String) extends JsonValue
case class JsonInt(value: Int) extends JsonValue
case class JsonFloat(value: Float) extends JsonValue
case class JsonBoolean(value: Boolean) extends JsonValue
object JsonNull extends JsonValue


object JsonWriter {
  def write(jsonValue: JsonValue): String = {
    jsonValue match {
      case JsonObject(entries) => ???
      case JsonList(entries) => ???
      case JsonString(value) => ???
      case JsonInt(value) => ???
      case JsonFloat(value) => ???
      case JsonBoolean(value) => ???
      case JsonNull => ???
   }
  }
}

```

* Write json type class for `String` and case `class User(firstName: String, age: Int)` and serialise it

* Scala syntax note
```scala
def write[A: Json](a: A) = ...
// is syntax sugar for this. You can use them interchangeably
def write[A](a: A)(implicit: Json[A]) = ...
```
Scala allows only one implicit method parameter list. And it has to be the last one.
It can be the first one in case method has only one parameter list.

If there are two type classes with the same type you get an `ambiguous implicit values` error.
They also can be passed explicitly which will resolve this issue. But it is generally bad idea to have two type classes
for the same type in a project.

The best idea is to put type class to companion object as this is always in scope when you import type and
you do not need to give them unique names.

* It is ugly to write
```scala
val user = User("vlejd", 22)
println(JsonWriter.write(implicitly[Json[User]].json(user)))
```
Put apply method to `Json` companion object to be able to do
```scala
val user = User("vlejd", 22)
println(JsonWriter.write(Json[User].json(user)))
```

* It is ugly to write
```scala
println(JsonWriter.write(implicitly[Json[User]].json(user)))
```
Create another write method on `JsonWriter` which will take anything that can be converted to json and produces `String` 
```scala
println(JsonWriter.write(user))
```

* It is ugly to write
```scala
val user = User("vlejd", 22)
println(JsonWriter.write(user))
```
Make implicit conversion to be able to write. Put it to `Json` companion object and call it `Ops`
```scala
val user = User("vlejd", 22)
println(user.toJson)
```

* Change also serialising of user fields to user this new `.toJson` method

* It is ugly to write
```scala
implicit val stringJson = new Json[String] {
  def json(a: String) = JsonString(a) 
}
```
Make helper method to be able to write type class instances like. Put it to `Json` companion object and call it `instance`
```scala
implicit val stringJson = Json.instance[String](a => JsonString(a)) 
// or
implicit val stringJson = Json.instance((a: String) => JsonString(a)) 
```

* Now you can convert many types to json but what if type is in a `Option` on in a `List`?
Write `Json` type class for anything which has `Json` type class and is wrapped in `Option` and another when it is wrapped in `List`.
This should be `def` not a `val` like before because values in scala are not polymorphic (they do not take type parameters)
```scala
// <write your code here> 
// 
// now you implement only int type class and you are able to serialise Options  
implicit val intJson = Json.instance[Int](a => JsonNumber(a))
println(JsonWriter.write(Some(122)))

// it is easy also to change User serialising that age is optional
// you make only change in User case class and everything works. If you made more changes somewhere you have a problem
class User(firstName: String, age: Option[Int])
```

* Type classes are resolved in compile time and are taken from the scope where the code implicitly needs them.
Write stringReverseJson type class and make `User` to accept this type class for `Json[String]` from the scope
where you serialise it
```scala
implicit val stringReverseJson = Json.instance[String](a => JsonString(a.reverse)) 
```

* Bonus task: Do implicit type class for every case class with help of `shapeless.LabelledGeneric`
```scala
class User(firstName: String, age: Option[Int])

object User {
  val json = AutoJson.instance[User]
} 
```