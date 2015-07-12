package spray.demo

import akka.actor.{Props, ActorSystem}
import scala.concurrent.duration._
import akka.io.IO
import akka.util.Timeout
import spray.can.Http
import spray.demo.db.{Repo, Db}
import spray.demo.rest.RoutingActor

/**
 * Created by itilk on 7/12/15.
 */
object Demo extends App{
    implicit val timeout = Timeout(10.seconds)
    implicit val system = ActorSystem.create("demo-actor-system")

    //initialize the database
    Repo.createSchema()

    // the handler actor replies to incoming HttpRequests
    val handler = system.actorOf(RoutingActor.props(timeout), name = "routing-service")

    IO(Http) ! Http.Bind(handler, interface = "0.0.0.0", port = 8080)
}
