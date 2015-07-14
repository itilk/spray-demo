package spray.demo.rest

import akka.actor.{Props, ActorRef, Actor}
import akka.util.Timeout
import spray.routing.Route
import scala.concurrent.duration._

/**
 * Created by itilk on 7/12/15.
 */
object RoutingActor {
  def props(implicit askTimeout: Timeout): Props = Props(classOf[RoutingActor], askTimeout)
  def name = "router"
}

class RoutingActor(implicit val askTimeout: Timeout) extends Actor
  with LeagueService
  with PlayerService
  with TeamService {
  def actorRefFactory = context

  def receive: Receive = {
    val routes = leagueRoutes(context) ~ playerRoutes(context) ~ teamRoutes(context)
    runRoute(routes)
  }
}
