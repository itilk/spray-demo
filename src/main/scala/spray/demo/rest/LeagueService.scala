package spray.demo.rest

import spray.httpx.SprayJsonSupport
import spray.routing.{Route, HttpService}

/**
 * Created by itilk on 7/12/15.
 */
trait LeagueService extends HttpService with SprayJsonSupport {
  def leagueRoutes : Route = {
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
