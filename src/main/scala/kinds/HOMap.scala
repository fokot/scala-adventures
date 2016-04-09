//package kinds
//
//class Connected[K, V, A]() {
//
//}
//
//class HOMap[K[_], V[_], A] forSome {type A} private (underlying: Map[K[A], V[A]])(implicit a: Connected[K, V, A]) {
//
//  def add[A](key: K[A], value: V[A]) = new HOMap(underlying.updated(key, value))
//
////  def updated[T](key: K[T], value: V[T]) =
////    new HOMap(underlying.updated(key, value))
////
////  def remove(key: K[_]) = new HOMap(underlying - key)
////
////  def get[T](key: K[T]) = underlying.get(key).asInstanceOf[Option[V[T]]]
////
////  def apply[T](key: K[T]) = get(key) getOrElse { throw new IllegalArgumentException("No value for specified key") }
////
////  def + = updated _
////
////  def contains(key: K[_]) = underlying.contains(key)
//}
//
//
////object HOMap {
////  def apply = new HOMap _
////}
//
