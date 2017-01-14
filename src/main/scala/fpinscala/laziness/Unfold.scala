package fpinscala.laziness

object Unfold {

  def apply[A, S](z: S)(f: S => Option[(A, S)]): Stream[A] =
    f(z) match {
      case Some((h,s)) => Stream.cons(h, apply(s)(f))
      case None => Stream.empty
    }
}
