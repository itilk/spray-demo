package spray.demo.rest

import akka.actor.{ActorContext, ActorRef}
import akka.util.Timeout
import akka.pattern.ask
import spray.demo.db.{Repo}
import spray.demo.model.Player
import spray.httpx.SprayJsonSupport
import spray.routing.{HttpService, Route}
import spray.http.MediaTypes._
import spray.http.StatusCodes._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Failure}

/**
 * Created by itilk on 7/12/15.
 */
trait PlayerService extends HttpService with SprayJsonSupport {
  def playerRoutes(context: ActorContext)(implicit askTimeout: Timeout): Route = {
    implicit def ec = context.dispatcher
    import DemoJsonProtocol._
    pathPrefix("demo") {
      pathPrefix("player") {
        get {
          path(IntNumber) { id =>
            rejectEmptyResponse {
              respondWithMediaType(`application/json`) {
                onComplete(Repo.getPlayer(id)) {
                  case Success(x) => complete {
                    x
                  }
                  case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
                }
              }
            }
          } ~
            pathSingleSlash {
              rejectEmptyResponse {
                respondWithMediaType(`application/json`) {
                  import DemoJsonProtocol._
                  onComplete(Repo.getPlayers()) {
                    case Success(x) => complete {
                      x
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
              entity(as[Player]) { player =>
                onComplete(Repo.createPlayer(player)) {
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
              import DemoJsonProtocol._
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
