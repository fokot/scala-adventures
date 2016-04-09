package forkjoin

import java.util.concurrent.{ForkJoinPool, RecursiveTask}

import utils.Stopwatch


/**
  * Testing if fork/join executor in java 7 is really faster than sequential evaluation
  */
object ForkJoinTest extends App {

  val array = (0 until 100000000).toArray

  Stopwatch("fork-join 1") {
    val v = Sum.sumArray(array)
    println(v)
  }

  Stopwatch("sequential 1") {
    var i = 0
    var v: Long = 0
    val size = array.size
    while (i < size) {
      v += array(i)
      i += 1
    }
    println(v)
  }

  Stopwatch("fork-join 2") {
    val v = Sum.sumArray(array)
    println(v)
  }

  Stopwatch("sequential 2") {
    var i = 0
    var v: Long = 0
    val size = array.size
    while (i < size) {
      v += array(i)
      i += 1
    }
    println(v)
  }
}

object Sum {
  val SEQUENTIAL_THRESHOLD: Int = 5000

  var fjPool = new ForkJoinPool

  def sumArray(array: Array[Int]): Long = {
    fjPool.invoke(new Sum(array, 0, array.length))
  }
}

case class Sum(array: Array[Int], low: Int, high: Int) extends RecursiveTask[Long] {

  protected def compute: Long = {
    if (high - low <= Sum.SEQUENTIAL_THRESHOLD) {
      var sum: Long = 0
      var i = low
      while (i < high) {
        sum += array(i)
        i += 1
      }
      sum
    } else {
      val mid = low + (high - low) / 2
      val left = new Sum(array, low, mid)
      val right = new Sum(array, mid, high)
      left.fork
      val rightAns = right.compute
      val leftAns = left.join
      leftAns + rightAns
    }
  }
}