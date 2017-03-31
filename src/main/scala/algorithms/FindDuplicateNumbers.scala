package algorithms

// https://leetcode.com/problems/find-the-duplicate-number
object FindDuplicateNumbers extends App {

  val numbers = List(1, 3, 2, 4, 2)

  def findDuplicateNumber(nums: List[Int]) = numbers.sum - (1 until nums.size).sum

  println(findDuplicateNumber(numbers))
}
