package algorithms

/**
  * Tail-recursion if Scala had no tail-recursion
  */
object FactorialTailRecursionWithEither {

  def factorial(n: Int): Int = {
    def factorialT(n: Int, acc: Int): Either[(Int, Int), Int] =
      if (n == 0)
        Right(0)
      else
        Left(n-1, acc * n)
    tailrec((n, 1), factorialT)
  }

  // A is recursive function parameter
  // B is recursive function result type
  def tailrec[A, B](v: (A, B), fun: (A, B) => Either[(A, B), B]): B = {
    var result: Either[(A, B), B] = Left(v)
    do {
//      println(result)
      val (parameter, accumulator) = result.left.get
      result = fun(parameter, accumulator)
    } while (result.isLeft)

    result.right.get
  }

  def main(args: Array[String]) {
    println(factorial(5))
  }
}