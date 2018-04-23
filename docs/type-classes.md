# Type classes and patterns

* **type class** - pattern to extend classes
* **type class** = trait with type parameter + implicit implementations as type class instances
* resolved (all instances are known) in **compile time**
* in the example is `Json` type class with instances for `String` and `Double`
* usually type class instances are in **instances** package e.g. `import cats.instances._`
```scala
trait Json[A] {
    def toJson(a: A): JSModel
}

implicit val jsonString = new Json[String]{
    def toJson(a: String) = JSString(a)
}

implicit val jsonDouble = new Json[Double]{
    def toJson(a: Double) = JSNumber(a)
}
```
## dependent type classes
```scala
//implicit def jsonOption[A](implicit jsonA: Json[A]) =
// this is the same as

implicit def jsonOption[A: Json] = 
    new Json[Option[A]]{
        def toJson(a: Option[A]) = a.map(implicitly[Json[A]].toJson).getOrElse(JSNull)
    }
```

## it can also depend on different type class
```scala
// we can automatically have type class Json for type A
// if we have Enum type class for it 
implicit def jsonOption[A: Enum] = new Json[A]{ ... }
```

## instance pattern
* creates type class instances
```scala
object Json {
    def instance[A](f: A => JSModel) =
        new Json[Option[A]]{
          def toJson(a: A) = f(a)
        }
}

implicit val intJson = Json.instance[Int](JSNumber(_))
```

## summoner pattern
* summons type class instance to scope
```scala
object Json {
    def apply[A : Json] = implicitly[Json[A]]
}

implicit def jsonOption[A: Json] = 
    new Json[Option[A]]{
        def toJson(a: Option[A]) = a.map(Json[A].toJson).getOrElse(JSNull)
    }
```

## ops pattern
* implicit conversion to make type class look like methods on object
* usually implicit conversions are in **syntax** package e.g. `import cats.syntax.all._` or `import cats.syntax.option._` to be able to use `.some` and `import cats.syntax.either._` to be able to use `.asLeft` and `.asRight`
```scala
object Json {
    implicit class Ops[A : Json](a: A) {
      def toJson = Json[A].toJson(a)
    }
}
 
import Json.Ops
  
Json[String].toJson("asdfasdf")  
// you can write the same now as   
"asdfasdf".toJson
1.toJson
```

## companion object with instances pattern
* bring type class instances always to scope when importing object
```scala
case class User(id: ID, firstName: String, lastName: String, active: Boolean)
 
object User {
    implicit val json = Json.instance[User](u => ???)
} 

import User
// now is also type class Json[User] in scope
```

## debugging type classes

* in compile time (check for compilations errors)

```scala
implicitly[Json[A]]
```  

* in runtime (print resolved type classes)

```scala
import scala.reflect.runtime.universe._

println(reify(Json[Int]))
//> Expr[AAA.Json[Int]](AAA.this.Json.apply[Int](AAA.this.intJson))
```
