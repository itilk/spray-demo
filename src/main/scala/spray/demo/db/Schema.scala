package spray.demo.db

import slick.lifted.ForeignKeyQuery
import spray.demo.model._

/**
 * Created by itilk on 7/7/15.
 */
object Schema {

  //import the slick driver api classes

  import Db.slickDriver.api._

  class Leagues(tag: Tag) extends Table[League](tag, "LEAGUES") {
    def id = column[Int]("LEAGUE_ID", O.PrimaryKey, O.AutoInc)

    // This is the primary key column
    def name = column[String]("LEAGUE_NAME")

    def sport = column[String]("LEAGUE_SPORT")

    def * = (id.?, name, sport) <>(
      (t: (Option[Int], String, String)) => League(t._1, t._2, t._3, Nil),
      (l: League) => Some((l.id, l.name, l.sport)))
  }

  val leagues = TableQuery[Leagues]

  class Conferences(tag: Tag) extends Table[Conference](tag, "CONFERENCES") {
    def id = column[Int]("CONF_ID", O.PrimaryKey, O.AutoInc)// This is the primary key column
    def name = column[String]("CONF_NAME")
    def leagueId = column[Int]("CONF_LEAGUE_ID")

    def * = (id.?, name, leagueId) <>(
      (t: (Option[Int], String, Int)) => Conference(t._1, t._2, t._3, Nil),
      (c: Conference) => Some((c.id, c.name, c.leagueId)))

    def league: ForeignKeyQuery[Leagues, League] =
      foreignKey("LEAGUE_FK", leagueId, leagues)(_.id)
  }

  val conferences = TableQuery[Conferences]

  class Divisions(tag: Tag) extends Table[Division](tag, "DIVISIONS") {
    def id = column[Int]("DIV_ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("DIV_NAME")
    def confId = column[Int]("DIV_CONF_ID")

    def * = (id.?, name, confId) <>(
      (t: (Option[Int], String, Int)) => Division(t._1, t._2, t._3, Nil),
      (d: Division) => Some((d.id, d.name, d.conferenceId)))

    def conference: ForeignKeyQuery[Conferences, Conference] =
      foreignKey("CONFERENCE_FK", confId, conferences)(_.id)
  }

  val divisions = TableQuery[Divisions]

  class Teams(tag: Tag) extends Table[Team](tag, "TEAMS") {
    def id = column[Int]("TEAM_ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("TEAM_NAME")
    def city = column[String]("TEAM_CITY")
    def divId = column[Int]("TEAM_DIV_ID")

    def * = (id.?, name, city, divId) <>(
      (t: (Option[Int], String, String, Int)) => Team(t._1, t._2, t._3, t._4, Nil),
      (t: Team) => Some((t.id, t.name, t.city, t.divisionId)))

    def division: ForeignKeyQuery[Divisions, Division] =
      foreignKey("DIVISION_FK", divId, divisions)(_.id)
  }

  val teams = TableQuery[Teams]

  class Players(tag: Tag) extends Table[Player](tag, "PLAYERS") {
    def id = column[Int]("PLAYER_ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("PLAYER_NAME")
    def number = column[Int]("PLAYER_NUMBER")
    def position = column[String]("PLAYER_POSITION")
    def teamId = column[Int]("PLAYER_TEAM_ID")

    def * = (id.?, name, number, position, teamId) <>(
      (t: (Option[Int], String, Int, String, Int)) => Player(t._1, t._2, t._3, t._4, t._5),
      (p: Player) => Some((p.id, p.name, p.number, p.position, p.teamId)))

    def team: ForeignKeyQuery[Teams, Team] =
      foreignKey("PLAYER_TEAM_FK", teamId, teams)(_.id)
  }

  val players = TableQuery[Players]

}
