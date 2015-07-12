package spray.demo.rest

import akka.actor.{ActorContext, ActorRef}
import spray.httpx.SprayJsonSupport
import spray.routing.{Route, HttpService}

/**
 * Created by itilk on 7/12/15.
 */
trait LeagueService extends HttpService with SprayJsonSupport {
  def leagueRoutes(context : ActorContext) : Route = {
    pathPrefix("demo") {
      pathPrefix("league") {
        pathSingleSlash{
          complete{
            "Leagues!"
          }
        }
      }
    }
  }
}
