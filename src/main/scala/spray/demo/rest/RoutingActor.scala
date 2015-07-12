package spray.demo.rest

import akka.actor.Actor
import akka.util.Timeout
import spray.routing.Route
import scala.concurrent.duration._

/**
 * Created by itilk on 7/12/15.
 */
class RoutingActor extends Actor with LeagueService {
  implicit val timeout = Timeout(10.seconds)
  def actorRefFactory = context

  def receive: Receive = {
    val routes = leagueRoutes
    runRoute(routes)
  }
}
