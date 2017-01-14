package fpinscala.testing

import fpinscala.state.StateExercises._
import fpinscala.state.State
import fpinscala.state.RNG
import fpinscala.state.RNG._
import fpinscala.laziness.Unfold
import Prop._

case class Prop(run: (MaxSize,TestCases,RNG) => Result) {

  def && (p: Prop): Prop = Prop(
    (m, n,rng) =>  this.run(m, n, rng) match {
      case Passed => p.run(m, n,rng)
      case f => f
    }
  )

  def || (p: Prop): Prop = Prop(
    (m, n,rng) =>  this.run(m, n, rng) match {
      case Falsified(msg, _) => p.tag(msg).run(m, n,rng)
      case p => p
    }
  )

  def tag(msg: String): Prop = Prop (
    (m, n, rng)  => run(m, n, rng) match {
      case Falsified(f, s) => Falsified(s"msg\nf", s)
      case p => p
    }
  )
}

object Prop {
  type FailedCase = String
  type SuccessCount = Int
  type TestCases = Int
  type MaxSize = Int

  sealed trait Result {
    def isFalsified: Boolean
  }

  case object Passed extends Result {
    override def isFalsified = false
  }

  case class Falsified(failure: FailedCase, successes: SuccessCount) extends Result {
    override def isFalsified = true
  }

  def apply(f: (TestCases,RNG) => Result): Prop =
    Prop { (_,n,rng) => f(n,rng) }

  /* Produce an infinite random stream from a `Gen` and a starting `RNG`. */
  def randomStream[A](g: Gen[A])(rng: RNG): Stream[A] =
    Unfold(rng)(rng => Some(g.sample.run(rng)))

  def forAll[A](as: Gen[A])(f: A => Boolean): Prop = Prop {
    (n, rng) => randomStream(as)(rng).zip(Stream.from(0)).take(n).map {
      case (a, i) => try {
        if (f(a)) Passed else Falsified(a.toString, i)
      } catch {
        case e: Exception => Falsified(buildMsg(a, e), i)
      }
    }.find(_.isFalsified).getOrElse(Passed)
  }

  def forAll[A](g: SGen[A])(f: A => Boolean): Prop =
    forAll(g(_))(f)

  def forAll[A](g: Int => Gen[A])(f: A => Boolean): Prop = Prop {
    (max,n,rng) =>
      val casesPerSize = (n - 1) / max + 1
      val props: Stream[Prop] =
        Stream.from(0).take((n min max) + 1).map(i => forAll(g(i))(f))
      val prop: Prop =
        props.map(p => Prop { (max, n, rng) =>
          p.run(max, casesPerSize, rng)
        }).toList.reduce(_ && _)
      prop.run(max,n,rng)
  }

  // String interpolation syntax. A string starting with `s"` can refer to
  // a Scala value `v` as `$v` or `${v}` in the string.
  // This will be expanded to `v.toString` by the Scala compiler.
  def buildMsg[A](s: A, e: Exception): String =
    s"test case: $s\n" +
    s"generated an exception: ${e.getMessage}\n" +
    s"stack trace:\n ${e.getStackTrace.mkString("\n")}"

  def run(p: Prop,
          maxSize: Int = 100,
          testCases: Int = 100,
          rng: RNG = RNG.Simple(System.currentTimeMillis)): Unit =
    p.run(maxSize, testCases, rng) match {
      case Falsified(msg, n) =>
        println(s"! Falsified after $n passed tests:\n $msg")
      case Passed =>
        println(s"+ OK, passed $testCases tests.")
  }

}



case class Gen[A](sample: State[RNG,A]) {

  def map[B](f: A => B): Gen[B] = Gen(sample.map(f))

  def map2[B, C](b: Gen[B])(f: (A, B) => C): Gen[C] = Gen(sample.map2(b.sample)(f))

  def flatMap[B](f: A => Gen[B]): Gen[B] = Gen(sample.flatMap( f(_).sample) )

  /* A method alias for the function we wrote earlier. */
  def listOfN(size: Int): Gen[List[A]] =
    Gen.listOfN(size, this)

  def listOfN(size: Gen[Int]): Gen[List[A]] = size.flatMap(n => Gen.listOfN(n, this))

  def unsized: SGen[A] = SGen(_ => this)

  def **[B](s2: Gen[B]): Gen[(A,B)] = (this map2 s2)( (_, _) )

}

object Gen {

  def choose(start: Int, stopExclusive: Int): Gen[Int] =
    Gen(State(nonNegativeInt).map(_ + start % (stopExclusive - start)))

  def unit[A](a: => A): Gen[A] = Gen(State.unit(a))

  def boolean: Gen[Boolean] = Gen(int.map(_ < 0))

  def listOfN[A](n: Int, g: Gen[A]): Gen[List[A]] = Gen(State.sequence(List.fill(n)(g.sample)))

  def union[A](g1: Gen[A], g2: Gen[A]): Gen[A] = boolean.flatMap( if (_) g1 else g2 )

  def weighted[A](g1: (Gen[A],Double), g2: (Gen[A],Double)): Gen[A] = {
    val g1Threshold: Int = (g1._2 * Integer.MAX_VALUE / (g1._2 + g2._2)).toInt
    Gen(State(nonNegativeInt)).flatMap(d => if(d <= g1Threshold) g1._1 else g2._1)
  }

  def listOf[A](g: Gen[A]): SGen[List[A]] = SGen(n => listOfN(n, g))

  def listOf1[A](g: Gen[A]): SGen[List[A]] =
    SGen(n => g.listOfN(n))

//  val maxProp1 = forAll(listOf1(smallInt)) { l =>
//    val max = l.max
//    !l.exists(_ > max) // No value greater than `max` should exist in `l`
//  }
}


case class SGen[A](forSize: Int => Gen[A]) {

  def apply(n: Int): Gen[A] = forSize(n)

  def map[B](f: A => B): SGen[B] = SGen(n => apply(n).map(f))

  def flatMap[B](f: A => SGen[B]): SGen[B] = SGen(n => apply(n).flatMap(f(_).apply(n)))

  def **[B](s2: SGen[B]): SGen[(A,B)] =
    SGen(n => apply(n) ** s2(n))
}



class Testing extends App {


  val intList = Gen.listOfN(10, Gen.choose(0,100))

  val prop = forAll(intList)(ns => ns.reverse.reverse == ns) &&
    forAll(intList)(ns => ns.headOption == ns.reverse.lastOption)

  val failingProp = forAll(intList)(ns => ns.reverse == ns)

//  println(prop.check)
//  println(failingProp.check)
}
