package zioplay

import zio.{Task, ZIO}

/*

type UIO[+A] = ZIO[Any, Nothing, A]
type Task[+A] = ZIO[Any, Throwable, A]
type IO[+E, +A] = ZIO[Any, E, A]

*/

object GraphQL {

  trait GEnvironment[A] {
    def value: A
  }

  trait GType[A] {
    def name: String
  }

  case class GObject[A, Env[_], E](
    name: String,
    fields: List[GField[A, Env, E, Any]],
  ) extends GType[A]

  case class GPrimitive[A](
    name: String,
  ) extends GType[A]

  object GInt extends GPrimitive[Int]("Int")

  case class ListType[A](
    `type`: GType[A]
  ) extends GType[List[A]] {
    override def name: String = s"List of ${`type`.name}"
  }

  // field in parent object A and returns type B
  case class GField[A, Env[_], E, +B](
    name: String,
    `type`: GType[B],
    resolver: ZIO[Env[A], E, B],
  )
}

object Domain {

  case class User(id: Int)

  case class Booking(id: Int)
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

  val userType = GObject[User, AllServices, Throwable](
    "User",
    List(
      GField[User, AllServices, Throwable, Int](
        "id",
        GInt,
//        ZIO.accessM(r => ZIO(r.value.id))
        ZIO.access(_.value.id)
      ),
    )
  )

  val rootType = GObject[Nothing, AllServices, Throwable](
  "root",
  List(
    GField[Nothing, AllServices, Throwable, List[User]](
      "users",
      ListType(userType),
      ZIO.accessM(_.allUsers)
    ),
    GField[Nothing, AllServices, Throwable, Int](
      "bookingCount",
      GInt,
      ZIO.access(_.bookingCount)
    ),
    )
  )



}
