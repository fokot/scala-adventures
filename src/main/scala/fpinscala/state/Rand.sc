trait RNG {
  // Should generate a random `Int`. We'll later define other functions in terms of `nextInt`.
  def nextInt: (Int, RNG)
}


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

type Rand[+A] = RNG => (A, RNG)

val int: Rand[Int] = _.nextInt

def unit[A](a: A): Rand[A] = rng => (a, rng)

def map[A,B](s: Rand[A])(f: A => B): Rand[B] = rng => {
  val (a, rng2) = s(rng)
  (f(a), rng2)
}

def nonNegativeInt2: Rand[Int] = map(int)(x => if(x < 0) -(x + 1) else x)

// Write a function to generate a Double between 0 and 1, not including 1.
// Note: You can use Int.MaxValue to obtain the maximum positive integer value, and you can use x.toDouble to convert an x: Int to a Double.
def double: Rand[Double] = map(nonNegativeInt2)(_.toDouble / Int.MaxValue)


def map2[A,B,C](ra: Rand[A], rb: Rand[B])(f: (A, B) => C): Rand[C] = rng => {
  val (a, rng1) = ra(rng)
  val (b, rng2) = rb(rng1)
  (f(a, b), rng2)
}

def both[A,B](ra: Rand[A], rb: Rand[B]): Rand[(A,B)] = map2(ra, rb)((_, _))

val randIntDouble: Rand[(Int, Double)] = both(int, double)

val randDoubleInt: Rand[(Double, Int)] = both(double, int)

val (d, i) = randDoubleInt(Simple(8))

def sequence[A](fs: List[Rand[A]]): Rand[List[A]] = rng =>
  fs.foldRight(unit(List[A]()))((ra, acc) => map2(ra, acc)(_ :: _))(rng)

double(Simple(5))
sequence(List(double, double, double))(Simple(5))

def flatMap[A,B](f: Rand[A])(g: A => Rand[B]): Rand[B] = rng => {
  val (a, rng2) = f(rng)
  g(a)(rng2)
}

def _map[A,B](s: Rand[A])(f: A => B): Rand[B] = flatMap(s)(a => unit(f(a)))

def _map2[A,B,C](ra: Rand[A], rb: Rand[B])(f: (A, B) => C): Rand[C] = flatMap(ra)(a => _map(rb)(b => f(a, b)))