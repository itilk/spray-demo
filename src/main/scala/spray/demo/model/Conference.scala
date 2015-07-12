package spray.demo.model

/**
 * Created by itilk on 7/10/15.
 */
case class Conference(id: Option[Int], name : String, leagueId: Int, divisions: Seq[Division])
