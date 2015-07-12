package spray.demo.model

/**
 * Created by itilk on 7/10/15.
 */
case class League(id: Option[Int], name: String, sport: String, conferences : Seq[Conference])
