package spray.demo

import akka.actor.{Props, ActorSystem}
import akka.io.IO
import spray.can.Http
import spray.demo.rest.RoutingActor

/**
 * Created by itilk on 7/12/15.
 */
object Demo extends App{
  implicit val system = ActorSystem.create("demo-actor-system")

  // the handler actor replies to incoming HttpRequests
  val handler = system.actorOf(Props(classOf[RoutingActor]), name = "routing-service")

  IO(Http) ! Http.Bind(handler, interface = "0.0.0.0", port = 8080)
}
