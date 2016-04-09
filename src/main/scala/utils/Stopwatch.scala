package utils

object Stopwatch {

  def apply(name: String)(f: => Unit) = {
    val start = System.currentTimeMillis
    f
    val end = System.currentTimeMillis
    println(name + " took " + (end - start) + "ms")
  }
}
