package algorithms

// https://leetcode.com/problems/find-the-duplicate-number
object FindDuplicateNumbers extends App {

  val numbers = List(1, 3, 2, 4, 2)

  def findDuplicateNumber(nums: List[Int]) = numbers.sum - (nums.size * (nums.size - 1) / 2)

  println(findDuplicateNumber(numbers))
}
