package various

import various.common._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class InCtx(headers: Map[String, String])
case class OutCtx(userName: String, roles: Set[String])

case class CreateUser(name: String)
case class User(id: Int, name: String)

object common {

  def parseTenant(in: InCtx): Option[String] = ???

  def parseCtx(in: InCtx): Option[OutCtx] = ???

  def serviceLogic(tenant: String, uc: CreateUser): Future[User] =
    Future.successful(User(1, uc.name))

  type RoleMatcher = OutCtx => Boolean

  val operatorMatcher: RoleMatcher = _.roles.contains("operator")

  type ServiceCall[A, B] = A => Future[B]

  def serverServiceCall[A, B](fn: InCtx => ServiceCall[A, B]): ServiceCall[A, B] = ???

}

object continuation {

  def hasTenant[Req, Res](context: InCtx)(fn: String => Req => Future[Res]): Req => Future[Res] = {
    val tenant = parseTenant(context)
    tenant match {
      case Some(tenant) => fn(tenant)
      case None => _ => Future.failed(new Exception("No tenant"))
    }
  }

  def authorized[Req, Res](matcher: RoleMatcher, context: InCtx)(fn: OutCtx => Req => Future[Res]): Req => Future[Res] = {
    val outCtx = parseCtx(context)
    outCtx match {
      case Some(outCtx) =>
        matcher(outCtx) match {
          case true => fn(outCtx)
          case false => _ => Future.failed(new Exception("Unauthorized"))
        }
      case None => _ => Future.failed(new Exception("Bad token"))
    }
  }

  def createUser(): ServiceCall[CreateUser, User] =
    serverServiceCall { inCtx =>
      hasTenant(inCtx) { tenant =>
        authorized(operatorMatcher, inCtx) { outCtx => createUserInput =>
          serviceLogic(tenant, createUserInput)
        }
      }
    }
}

object values {

  import cats.MonadError
  import cats.instances.future._

  implicit class OptionsOpts[A](fo: Option[A]) {
    def getOrRaise[F[_], E](e: => E)(implicit ME: MonadError[F, E]): F[A] =
      fo match {
        case Some(o) => ME.pure(o)
        case None => ME.raiseError(e)
      }
  }

  // this is super ugly :( but needed to transform serverServiceCall to Future
  def serverServiceCallFuture: Future[InCtx] =
    serverServiceCall[Unit, InCtx](inCtx => _ => Future.successful(inCtx))()

  def hasTenant[F[_]](context: InCtx)(implicit ME: MonadError[F, Throwable]): F[String] =
    parseTenant(context).getOrRaise[F, Throwable](new Exception("No tenant"))

  def authorized[F[_]](matcher: RoleMatcher, context: InCtx)(implicit ME: MonadError[F, Throwable]): F[OutCtx] =
    parseCtx(context).filter(matcher).getOrRaise[F, Throwable](new Exception("Unauthorized"))
//    parseCtx(context) match {
//      case Some(outCtx) => if(matcher(outCtx)) ME.pure(outCtx) else ME.raiseError(new Exception("Unauthorized"))
//      case None => _ => ME.raiseError(new Exception("Bad token"))
//    }

  def createUser(): ServiceCall[CreateUser, User] =
    createUserInput => for {
      inCtx  <- serverServiceCallFuture
      tenant <- hasTenant(inCtx)
      _      <- authorized(operatorMatcher, inCtx)
      res    <- serviceLogic(tenant, createUserInput)
    } yield res
}

object CompositionStyles extends App {

}
