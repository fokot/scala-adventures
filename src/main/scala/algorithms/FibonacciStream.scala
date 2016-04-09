package algorithms

object FibonacciStream extends App {

  //lazy stream Fibonnaci sequence
  val fib = {
    def fib(x: Int, y :Int) : Stream[Int] = x #:: fib(y, x + y)
    fib(1, 1)
  }

  fib.zipWithIndex.take(10).map(_.swap).foreach(println)
}
