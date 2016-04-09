import shapeless.test.illTyped

val typeUnsafeMap: Map[Option[Any], List[Any]] = Map(
  Some("foo") -> List("foo", "bar", "baz"),
  Some(42) -> List(1, 1, 2, 3, 5, 8),
  Some(true) -> List(true, false, true, true))

// ugly cast!
val xs: List[String] =
  typeUnsafeMap(Some("foo")).asInstanceOf[List[String]]

// ditto!
val ys: List[Int] =
  typeUnsafeMap(Some(42)).asInstanceOf[List[Int]]

// type parameters doesn't help neither
illTyped("""
def functionMap[A](a: A): List[A] = {
  case Some("foo") => List("foo", "bar", "baz")
  case Some(42) => List(1, 1, 2, 3, 5, 8)
  case Some(true) => List(true, false, true, true)
}
""")

// higher-order typed map

