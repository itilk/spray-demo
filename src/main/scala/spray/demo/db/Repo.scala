package spray.demo.db

import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.{Props, ActorRef, Actor}
import akka.util.Timeout
import spray.demo.model._

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.util.Try

/**
 * Created by itilk on 7/7/15.
// */

object Repo {
  import Db.slickDriver.api._
  import Schema.{leagues, conferences, divisions, teams, players}

  val database = Db.database

  def createSchema() = {
    val actions =
      (Schema.leagues.schema ++ Schema.conferences.schema ++ Schema.divisions.schema ++ Schema.teams.schema ++ Schema.players.schema).create >>
      (leagues += League(None, "MLB", "baseball", Nil)) >>
      (conferences += Conference(None, "National League", 1, Nil)) >>
      (conferences += Conference(None, "American League", 1, Nil)) >>
      (divisions += Division(None, "NL Central", 1, Nil)) >>
      (teams += Team(None, "Reds", "Cincinnati", 1, Nil)) >>
      (players += Player(None, "Todd Frazier", 21, "3B", 1))
    Await.result(database.run(actions), 10 seconds)

  }

  def createLeague(l: League): Try[Int] = {
    Try {
      Await.result(database.run(leagues returning leagues.map(_.id) += l), 10 seconds)
    }
  }

  def createConference(c: Conference): Try[Int] = {
    Try {
      Await.result(database.run(conferences returning conferences.map(_.id) += c), 10 seconds)
    }
  }

  def createDivision(d: Division): Try[Int] = {
    Try {
      Await.result(database.run(divisions returning divisions.map(_.id) += d), 10 seconds)
    }
  }

  def createTeam(t: Team): Try[Int] = {
    Try {
      Await.result(database.run(teams returning teams.map(_.id) += t), 10 seconds)
    }
  }

  def createPlayer(p: Player): Try[Int] = {
    Try {
      Await.result(database.run(players returning players.map(_.id) += p), 10 seconds)
    }
  }

  def getLeague(id: Int): Try[Option[League]] = {
    Try {
      val query = for {
        l <- leagues if l.id === id
        c <- conferences if (c.leagueId === l.id)
        d <- divisions if (d.confId === c.id)
        t <- teams if (t.divId === d.id)
        p <- players if (p.teamId === t.id)
      } yield (l, c, d, t, p)

      val result: Seq[(League, Conference, Division, Team, Player)] =
        Await.result(database.run(query.result), 10 seconds)

      result match {
        case Nil => None
        case _ => Some(parseLeagues(result).head)
      }
    }
  }

  def getLeagues(): Try[Option[Seq[League]]] = {
    Try {
      val query = for {
        l <- leagues
        c <- conferences if (c.leagueId === l.id)
        d <- divisions if (d.confId === c.id)
        t <- teams if (t.divId === d.id)
        p <- players if (p.teamId === t.id)
      } yield (l, c, d, t, p)

      val result: Seq[(League, Conference, Division, Team, Player)] =
        Await.result(database.run(query.result), 10 seconds)

      result match {
        case Nil => None
        case _ => Some(parseLeagues(result))
      }
    }
  }

  def getLeagueOuterJoin(id: Int) = {
    def q(id: Int) =
      sql"""SELECT l.LEAGUE_ID, l.LEAGUE_NAME, l.LEAGUE_SPORT, c.CONF_ID, c.CONF_NAME, c.CONF_LEAGUE_ID, d.DIV_ID, d.DIV_NAME,
                  d.DIV_CONF_ID, t.TEAM_ID, t.TEAM_NAME, t.TEAM_CITY, t.TEAM_DIV_ID,  p.PLAYER_ID, p.PLAYER_NAME,
                  p.PLAYER_NUMBER, p.PLAYER_POSITION, p.PLAYER_TEAM_ID
           FROM LEAGUES l
           LEFT JOIN CONFERENCES c ON l.LEAGUE_ID = c.CONF_LEAGUE_ID
           LEFT JOIN DIVISIONS d ON c.CONF_ID = d.DIV_CONF_ID
           LEFT JOIN TEAMS t on d.DIV_ID = t.TEAM_DIV_ID
           LEFT JOIN PLAYERS p on t.TEAM_ID = p.PLAYER_TEAM_ID
           WHERE l.LEAGUE_ID = #$id
        """.as[((Option[Int], String, String), (Option[Int], String, Int), (Option[Int], String, Int), (Option[Int], String, String, Int), (Option[Int], String, Int, String, Int))]

    val result = Await.result(database.run(q(id)), 10 seconds)

    val league =
      result.groupBy(_._1).map(
        a => League(a._1._1, a._1._2, a._1._3, a._2.groupBy(_._2).map(
          b => {
            b._1._1 match {
              case Some(_) => Some(Conference(b._1._1, b._1._2, b._1._3, b._2.groupBy(_._3).map(
                c => {
                  c._1._1 match {
                    case Some(_) => Some(Division(c._1._1, c._1._2, c._1._3, c._2.groupBy(_._4).map(
                      d => {
                        d._1._1 match {
                          case Some(_) => Some(Team(d._1._1, d._1._2, d._1._3, d._1._4, d._2.groupBy(_._5).map(
                            e => {
                              e._1._1 match {
                                case Some(_) => Some(Player(e._1._1, e._1._2, e._1._3, e._1._4, e._1._5))
                                case _ => None
                              }
                            }
                          ).toList.flatten))
                          case _ => None
                        }
                      }
                    ).toList.flatten))
                    case _ => None
                  }
                }
              ).toList.flatten))
              case _ => None
            }
          }
        ).toList.flatten)
      )
    println(league.headOption)
  }

  def parseLeagues(rows: Seq[(League, Conference, Division, Team, Player)]): Seq[League] = {
    rows.groupBy(_._1).map(
      x => League(x._1.id, x._1.name, x._1.sport, x._2.groupBy(_._2).map(
        y => Conference(y._1.id, y._1.name, y._1.leagueId, y._2.groupBy(_._3).map(
          z => Division(z._1.id, z._1.name, z._1.conferenceId, z._2.groupBy(_._4).map(
            i => Team(i._1.id, i._1.name, i._1.city, i._1.divisionId, i._2.groupBy(_._5).map(
              j => Player(j._1.id, j._1.name, j._1.number, j._1.position, j._1.teamId)
            ).toList)
          ).toList)
        ).toList)
      ).toList)
    ).toList
  }

  def getPlayers(): Future[Seq[Player]] = {
    val q = for {
      p <- players
    } yield p
    database.run(q.result)
  }

  def getPlayer(id: Int) : Future[Option[Player]] = {
    val q = for {
      p <-players if p.id === id
    } yield p

    database.run(q.result.headOption)
  }

  //  def deletePlayer(p: Player) : Try[Int] = {
  //    Try{
  //      val delete = players.filter(_.id === p.id).delete
  //      Await.result(database.run(delete), 10 seconds)
  //    }
  //  }

  //  def createPlayer(player: Player) : Try[Int]= {
  //    Try {
  //      Await.result(database.run(players += player), 10 seconds)
  //    }
  //  }
  //
  //  def createTeam(team: Team) : Try[Int]= {
  //    Try {
  //      Await.result(database.run(teams returning teams.map(_.id) += team), 10 seconds)
  //    }
  //  }
  //
  //  def createCoach(coach: Coach) : Try[Int]= {
  //    Try {
  //      Await.result(database.run(coaches += coach), 10 seconds)
  //    }
  //  }
  //
  //  def updatePlayer(player: Player) : Try[Int] = {
  //    Try {
  //      val updateQuery = for {
  //        p <- players if p.id === player.id
  //      } yield (p.name, p.number, p.position, p.teamId)
  //
  //      val updateAction = updateQuery.update((player.name, player.number, player.position, player.team.get.id.get))
  //      Await.result(database.run(updateAction), 10 seconds)
  //    }
  //  }
  //
  //  def deletePlayer(p: Player) : Try[Int] = {
  //    Try{
  //      val delete = players.filter(_.id === p.id).delete
  //      Await.result(database.run(delete), 10 seconds)
  //    }
  //  }
  //
  //  def getPlayers() : Try[List[Player]] = {
  //    Try{
  //      val query = for{
  //        p <- players
  //        t <- p.team
  //      } yield (p, t)
  //
  //      Await.result(database.run(query.result).map(toPlayers), 10 seconds)
  //    }
  //  }
  //
  //  def getTeams() : Try[Seq[Team]] = {
  //    Try{
  //      val query = for{
  //        t <- teams
  //      } yield t
  //
  //      Await.result(database.run(query.result).map(t => t), 10 seconds)
  //    }
  //  }
  //
  //  def getPlayer(id : Int) : Try[Player] = {
  //    Try {
  //      val query = for {
  //        p <- players if p.id === id
  //        t <- p.team
  //      } yield (p, t)
  //
  //      Await.result(database.run(query.result.head).map(toPlayer), 10 seconds)
  //    }
  //  }
  //
  //  def getTeam(id : Int) : Try[Team] = {
  //    Try {
  //      val query = teams.filter(_.id === id)
  //      Await.result(database.run(query.result.head).map(t => t), 10 seconds)
  //    }
  //  }
  //
  //
  //
  //  private def toPlayer(x : (Player, Team)) : Player = {
  //    Player(x._1.id, x._1.name, x._1.number, x._1.position, Some(x._2))
  //  }
  //
  //  private def toPlayers(rows : Seq[(Player, Team)]) : List[Player] = {
  //    rows.map(
  //      x => toPlayer(x)
  //    ).toList
  //  }
}
