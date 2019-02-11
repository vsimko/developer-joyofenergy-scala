import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

object WebServer

class WebServer(val route: Route, val host: String = "localhost", val port: Int = 8080) {
  implicit val system: ActorSystem = ActorSystem("joi-energy-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  def start(): RunningServer = {
    new RunningServer(Http().bindAndHandle(route, host, port))
  }

  class RunningServer(bindingFuture: Future[Http.ServerBinding]) {
    def stop(): Unit = {
      bindingFuture
        .flatMap(_.unbind())
        .onComplete(_ => system.terminate())
    }

    def stopOnReturn(): Unit = {
      println(s"Server online at http://localhost:$port/\nPress RETURN to stop...")
      StdIn.readLine()
      stop()
    }
  }

}
