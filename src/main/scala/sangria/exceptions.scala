//import org.http4s.Status
//
//import scala.util.control.NoStackTrace
//
//object exceptions {
//
//  case class GQLError(message: String, code: Int) extends Exception with NoStackTrace
//
//  def requestNotParsable(e: String) = GQLError(s"Error parsing request $e", 1)
//  def queryNotParsable(e: String) = GQLError(s"Can not parse query: $e", 2)
//  val Unauthorized = GQLError("Unauthorized", Status.Unauthorized.code)
//}
