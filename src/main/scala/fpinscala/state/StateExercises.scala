package fpinscala.state


object State {

  def unit[S,A](a: A): State[S,A] = State(s => (a, s))

  def sequence[S,A](fs: List[State[S,A]]): State[S,List[A]] =
    fs.foldRight(unit[S,List[A]](List[A]()))( (a, acc) => a.map2(acc)(_ :: _))

  def get[S]: State[S, S] = State(s => (s, s))

  def set[S](s: S): State[S, Unit] = State(_ => ((), s))

  def modify[S](f: S => S): State[S, Unit] = for {
    s <- get // Gets the current state and assigns it to `s`.
    _ <- set(f(s)) // Sets the new state to `f` applied to `s`.
  } yield ()
}

import State._

case class State[S,+A](run: S => (A,S)) {

  def map[B](f: A => B): State[S,B] = flatMap(a => unit(f(a)))

  def flatMap[B](f: A => State[S,B]): State[S,B] = State(
    s => {
      val (a, s2) = run(s)
      f(a).run(s2)
    }
  )

  def map2[B,C](b: State[S,B])(f: (A, B) => C): State[S,C] =
    flatMap(a => b.map(bval => f(a, bval)))
}

object StateExercises extends App {

  type Rand[A] = State[RNG, A]

  val int: Rand[Int] = State(_.nextInt)

  def ints(n: Int): Rand[List[Int]] = sequence(List.fill(n)(int))

  val ns: Rand[List[Int]] = for
  { x <- int
    y <- int
    xs <- ints(x)
  } yield xs.map(_ % y)

  // The rules of the machine are as follows:
  // * Inserting a coin into a locked machine will cause it to unlock if there’s any candy left.
  // * Turning the knob on an unlocked machine will cause it to dispense candy and become locked.
  // * Turning the knob on a locked machine or inserting a coin into an unlocked machine does nothing.
  // * A machine that’s out of candy ignores all inputs.
  //
  // For example, if the input Machine has 10 coins and 5 candies, and a total of 4 candies are
  // successfully bought, the output should be (14, 1).
  object CandyMachine {

    sealed trait Input
    case object Coin extends Input
    case object Turn extends Input

    case class Machine(locked: Boolean, candies: Int, coins: Int)

    def update(i: Input)(s: Machine) = (i, s) match {
      case (Coin, Machine(true, candies, coins)) if candies > 0 => Machine(false, candies, coins + 1)
      case (Turn, Machine(false, candies, coins)) => Machine(true, candies - 1, coins)
      case _ => s
    }

    def simulateMachine(inputs: List[Input]): State[Machine, (Int, Int)] = for {
      _ <- sequence( inputs map (modify[Machine] _ compose update))
      s <- get
    } yield (s.coins, s.candies)
  }

  import CandyMachine._

  val result = simulateMachine(List(Coin, Coin, Coin, Coin, Turn, Turn, Coin, Turn, Coin, Turn, Coin, Turn, Turn))
    .run(Machine(true, 5, 10))._1
  println(s"coins: ${result._1}, candies: ${result._2}")
}




