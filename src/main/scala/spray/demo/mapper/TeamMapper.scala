package spray.demo.mapper

import spray.demo.model.{Player, Team}

/**
 * Created by itilk on 7/14/15.
 */
trait TeamMapper {

  def parseTeams(rows : Seq[(Team, Option[Player])]) : Seq[Team] = {
    rows.groupBy(_._1).map(
      t => Team(t._1.id, t._1.name, t._1.city, t._1.divisionId, t._2.groupBy(_._2).map(_._1).toList.flatten)
    ).toList
  }
}
