//import io.circe.syntax._
//import cats.syntax.either._
//import cats.syntax.monadError._
//import cats.syntax.try_._
//import cats.syntax.option._
//import io.circe.optics.JsonPath.root
//import cats.effect.{ContextShift, IO}
//import com.upstartcommerce.rar.exceptions.GQLError
//import io.circe.{Decoder, Json}
//import io.circe.generic.semiauto
//import org.http4s.{EntityEncoder, Request, Response, Status}
//import org.http4s.dsl.io._
//import org.http4s.headers.Authorization
//import org.http4s.circe._
//import sangria.ast.Document
//import sangria.execution.{BeforeFieldResult, ExceptionHandler, Executor, HandledException, Middleware, MiddlewareBeforeField, MiddlewareQueryContext}
//import sangria.parser.QueryParser
//import sangria.marshalling.circe._
//import com.upstartcommerce.tokenauthsupport.auth
//import sangria.schema.{Context, FutureValue}
//
//import scala.concurrent.{ExecutionContext, Future}
//
//object gqlExecutor {
//
//  object AuthorizationMiddleware extends MiddlewareBeforeField[GQLContext] {
//    type QueryVal = Unit
//    type FieldVal = Unit
//
//    def beforeQuery(context: MiddlewareQueryContext[GQLContext, _, _]) = ()
//    def afterQuery(queryVal: QueryVal, context: MiddlewareQueryContext[GQLContext, _, _]) = ()
//
//    def beforeField(queryVal: QueryVal, mctx: MiddlewareQueryContext[GQLContext, _, _], ctx: Context[GQLContext, _]) = {
//      if (ctx.field.tags.contains(Authorized) && ctx.ctx.token.isEmpty)
//        throw exceptions.Unauthorized
//      else
//        continue
//    }
//  }
//
//  val exceptionHandler = ExceptionHandler {
//    case (m, e: GQLError) => HandledException.single(e.getMessage, Map("code" -> m.scalarNode(e.code, "Int", Set.empty)))
//  }
//
//  def executeWithSchema(queryAst: Document, context: GQLContext, operationName: Option[String], variables: Json, profiler: Boolean = false)(
//      implicit ec: ExecutionContext
//  ): Future[Json] = {
//
//    //    val includeMetrics = profiler match {
//    //      case Some("slowlog") => SlowLog.extension :: SlowLog.apolloTracing :: Nil
//    //      case _ => Nil
//    //    }
//
//    val middleware = AuthorizationMiddleware :: Nil
//
//    Executor.execute(
//      gqlSchema.schema,
//      queryAst,
//      context,
//      (),
//      operationName,
//      variables,
//      exceptionHandler = exceptionHandler,
//      middleware = middleware
//      //      deferredResolver = deferredResolver
//    )
//  }
//
//  val EMPTY_JSON = Json.fromFields(Nil)
//
//  case class GQLRequest(query: String, operationName: Option[String], variables: Option[Json])
//  implicit val decoder: Decoder[GQLRequest] = semiauto.deriveDecoder
//
//  def skipComments(s: String): String =
//    s.linesIterator.filterNot(l => l.trim.startsWith("#")).mkString("\n")
//
//  def isUnauthorized(j: Json) = root.errors.each.extensions.code.int.getAll(j).contains(Status.Unauthorized.code)
//
//  def executeRequest(req: Request[IO])(implicit CS: ContextShift[IO]): IO[Response[IO]] = {
//    val authorizationHeader: Option[Authorization] = req.headers.get(Authorization)
//    val token = authorizationHeader.flatMap(a => auth.decodeTokenPayload(a.value).toOption)
//    for {
//      reqJson <- req.as[Json]
//      gqlRequest <- reqJson.as[GQLRequest].leftMap(e => exceptions.requestNotParsable(e.message)).getOrRaise[IO, Throwable]
//      variables = gqlRequest.variables.filterNot(_.isNull).getOrElse(EMPTY_JSON)
//      queryAst <- QueryParser.parse(skipComments(gqlRequest.query)).liftTo[IO].adaptError {
//        case e => exceptions.queryNotParsable(e.getMessage)
//      }
//      // FIXME ExecutionContext
//      gqlResult <- IO.fromFuture(
//        IO.delay(executeWithSchema(queryAst, GQLContext(token), gqlRequest.operationName, variables)(ExecutionContext.global)      gqlResult <- IO.fromFuture(
//        IO.delay(executeWithSchema(queryAst, GQLContext(token, storage), gqlRequest.operationName, variables)(ExecutionContext.global).recover {
//          case error: QueryAnalysisError => {
//            error.resolveError
//          }
//        }(ExecutionContext.global))
//      )
//      response <- if (isUnauthorized(gqlResult))
//        IO(Response[IO](Status.Unauthorized, body = EntityEncoder[IO, Json].toEntity(gqlResult).body))
//      else
//        Ok(gqlResult)
//    } yield response
//  }
//
//}
