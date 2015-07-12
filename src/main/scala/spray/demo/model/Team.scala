package spray.demo.model

/**
 * Created by itilk on 7/7/15.
 */
case class Team(id: Option[Int], name: String, city: String, divisionId : Int, players : Seq[Player])
