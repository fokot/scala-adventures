package algorithms

object RemoveDuplicates extends App {

  def removeDups(l : List[Int]): List[Int] = l match {
    case a :: b :: xs if a == b => removeDups(b :: xs)
    case a :: b :: xs if a != b => a :: removeDups(b :: xs)
    case _ => l
  }

  println(removeDups(List(1, 2, 2, 2, 3, 4, 4)))
}
