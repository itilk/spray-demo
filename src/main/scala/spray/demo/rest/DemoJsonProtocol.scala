package spray.demo.rest

import spray.demo.model.Player
import spray.json._

/**
 * Created by itilk on 7/12/15.
 */
object DemoJsonProtocol extends DefaultJsonProtocol {
  implicit val playerJsonProtocol = jsonFormat5(Player)
}
