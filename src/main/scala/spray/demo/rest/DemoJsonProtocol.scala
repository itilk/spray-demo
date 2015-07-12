package spray.demo.rest

import spray.demo.model._
import spray.json._

/**
 * Created by itilk on 7/12/15.
 */
object DemoJsonProtocol extends DefaultJsonProtocol {
  implicit val playerJsonProtocol = jsonFormat5(Player)
  implicit val teamJsonProtocol = jsonFormat5(Team)
  implicit val divisionJsonProtocol = jsonFormat4(Division)
  implicit val conferenceJsonProtocol = jsonFormat4(Conference)
  implicit val leagueJsonProtocol = jsonFormat4(League)
}
