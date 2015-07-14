package spray.demo.rest

import akka.actor.ActorContext
import akka.util.Timeout
import spray.demo.db.Repo
import spray.demo.mapper.TeamMapper
import spray.demo.model.{Team, Player}
import spray.http.MediaTypes._
import spray.http.StatusCodes._
import spray.httpx.SprayJsonSupport
import spray.routing.{HttpService, Route}

import scala.util.{Failure, Success}

/**
 * Created by itilk on 7/12/15.
 */
trait TeamService extends HttpService with SprayJsonSupport with TeamMapper {
  def teamRoutes(context: ActorContext)(implicit askTimeout: Timeout): Route = {
    implicit def ec = context.dispatcher
    import DemoJsonProtocol._
    pathPrefix("demo") {
      pathPrefix("team") {
        get {
          path(IntNumber) { id =>
            rejectEmptyResponse {
              respondWithMediaType(`application/json`) {
                onComplete(Repo.getTeam(id)) {
                  case Success(x) => complete {
                    parseTeams(x).headOption
                  }
                  case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
                }
              }
            }
          } ~
            pathSingleSlash {
              rejectEmptyResponse {
                respondWithMediaType(`application/json`) {
                  onComplete(Repo.getTeams()) {
                    case Success(x) => complete {
                      parseTeams(x)
                    }
                    case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
                  }
                }
              }
            }
        } ~
          post {
            pathSingleSlash {
              import DemoJsonProtocol._
              entity(as[Team]) { team =>
                onComplete(Repo.createTeam(team)) {
                  case Success(x) => complete {
                    x.toString()
                  }
                  case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
                }
              }
            }
          } ~
          put {
            pathSingleSlash {
              import DemoJsonProtocol._
              entity(as[Player]) { player =>
                onComplete(Repo.updatePlayer(player)) {
                  case Success(x) => complete {
                    x.toString()
                  }
                  case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
                }
              }
            }
          } ~
          delete {
            path(IntNumber) { id =>
              onComplete(Repo.deletePlayer(id)) {
                case Success(x) => complete {
                  x.toString()
                }
                case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
              }
            }
          }
      }
    }
  }
}
