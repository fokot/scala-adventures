package utils

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
//import akka.http.scaladsl.util.FastFuture

@SuppressWarnings(Array("org.wartremover.warts.All"))
object FuturesTest extends App {

  implicit val ec: ExecutionContext = new ExecutionContext {

    override def execute(runnable: Runnable): Unit = {
      println("running")
      ExecutionContext.global.execute(runnable)
    }

    override def reportFailure(cause: Throwable): Unit =
      ExecutionContext.global.reportFailure(cause)
  }

  val f = Future { 1 + 1 }.flatMap(x => Future { x * 3 } )
  Await.result(f, Duration.Inf)

  println("--------------")
  val f2 = Future { 1 + 1 }.map(x => x * 3)
  Await.result(f2, Duration.Inf)

  println("--------------")
  val f3 = Future { 1 + 1 }.flatMap(x => Future.successful( x * 3 ) )
  Await.result(f3, Duration.Inf)

//  println("--------------")
//  val f4 = FastFuture.successful( 1 + 1 ).flatMap(x => Future.successful( x * 3 ) )
//  Await.result(f4, Duration.Inf)

  println("--------------")
  val f5 = Future.successful( 1 + 1 ).flatMap(x => Future.successful( x * 3 ) )
  Await.result(f5, Duration.Inf)
}
