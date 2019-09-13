package zioplay

import zio.{Task, ZIO}

/*

type UIO[+A] = ZIO[Any, Nothing, A]
type Task[+A] = ZIO[Any, Throwable, A]
type IO[+E, +A] = ZIO[Any, E, A]

*/

object GraphQL {

  trait GEnvironment[A] {
    value: A
  }

  case class GObject[A, Env <: GEnvironment[A], E](
    name: String,
    fields: List[GField[A, Env, E, Any]]
  )

  // field in parent object A or type B
  case class GField[A, Env[_], E, +B](
    name: String,
    resolver: ZIO[Env[A], E, B]
//    or this ?
//    resolver: Env[A] => ZIO[Env[A], E, B]
  )
}

object Domain {

  case class User(id: Long)

  case class Booking(id: Long)
}

object BookingGraphQL {

  import zioplay.Domain._
  import zioplay.GraphQL._

  trait UserService {
    def allUsers: Task[List[User]] = ???
  }

  trait BookingService {
    def bookingsForUser(u: User): Task[List[Booking]] = ???

    def bookingCount: Int = ???
  }

  type AllServices[A] = UserService with BookingService with GEnvironment[A]

//  val root = GObject[Nothing, AllServices[Nothing], Throwable](
//    "root",
//    List(
//      GField(
//        "users",
//        ZIO(null).accessM
//      )
//    )
//  )
}
