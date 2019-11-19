//import cats.MonadError
//
//object utils {
//
//  implicit class OptionsOps[A](fo: Option[A]) {
//    def getOrRaise[F[_], E](e: => E)(implicit ME: MonadError[F, E]): F[A] =
//      fo match {
//        case Some(o) => ME.pure(o)
//        case None => ME.raiseError(e)
//      }
//  }
//
//  implicit class EitherOps[A, E](fo: Either[E, A]) {
//    def getOrRaise[F[_], EE >: E](implicit ME: MonadError[F, EE]): F[A] =
//      fo match {
//        case Right(o) => ME.pure(o)
//        case Left(e) => ME.raiseError(e)
//      }
//  }
//}
