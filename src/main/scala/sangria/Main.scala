//import cats.effect._
//import cats.syntax.functor._
//import org.http4s._
//import org.http4s.dsl.io._
//import org.http4s.implicits._
//import org.http4s.server.Server
//import org.http4s.server.blaze._
//
//object Main extends IOApp {
//
//  override def run(args: List[String]): IO[ExitCode] =
//    app.use(_ => IO.never).as(ExitCode.Success)
//
//  def staticResource(path: String)(implicit blocker: Blocker): IO[Response[IO]] =
//    StaticFile.fromResource[IO](path, blocker).getOrElseF(NotFound())
//
//  def routes(implicit blocker: Blocker): HttpApp[IO] =
//    HttpRoutes
//      .of[IO] {
//        case req @ POST -> Root / "graphql" => gqlExecutor.executeRequest(req)
//        case GET -> Root / "graphiql" => staticResource("/static/graphiql.html")
//        case GET -> Root / "login.html" => staticResource("/static/login.html")
//      }
//      .orNotFound
//
////  val errorHandler: ServiceErrorHandler[IO] = _ => {
////    case GQLError(status, message) => IO(Response[IO](status, body = EntityEncoder[IO, Json].toEntity(message.asJson).body))
////  }
//
//  val app: Resource[IO, Server[IO]] =
//    for {
//      blocker <- Blocker[IO]
//      server <- BlazeServerBuilder[IO]
//        .bindHttp(8080)
//        .withHttpApp(routes(blocker))
//        //        .withServiceErrorHandler(errorHandler)
//        .resource
//    } yield server
//}
