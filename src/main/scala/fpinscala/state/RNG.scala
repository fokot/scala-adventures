package fpinscala.state

trait RNG {
  // Should generate a random `Int`. We'll later define other functions in terms of `nextInt`.
  def nextInt: (Int, RNG)
}

object RNG {

  case class Simple(seed: Long) extends RNG {
    def nextInt: (Int, RNG) = {
      // `&` is bitwise AND. We use the current seed to generate a new seed.
      val newSeed = (seed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL
      // The next state, which is an `RNG` instance created from the new seed.
      val nextRNG = Simple(newSeed)
      // `>>>` is right binary shift with zero fill. The value `n` is our new pseudo-random integer.
      val n = (newSeed >>> 16).toInt
      // The return value is a tuple containing both a pseudo-random integer and the next `RNG` state.
      (n, nextRNG)
    }
  }

  def nonNegativeInt(rng: RNG): (Int, RNG) = {
    val (i, r) = rng.nextInt
    (if (i < 0) -(i + 1) else i, r)
  }
}