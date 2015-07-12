package spray.demo.test

import org.scalatest.time.{Seconds, Span}
import org.scalatest.{FunSuite, BeforeAndAfter}
import org.scalatest.concurrent.ScalaFutures
import spray.demo.db.{Repo, Schema, Db}
import spray.demo.model._

import scala.util.Success

/**
 * Created by itilk on 7/9/15.
 */
class SchemaTest extends FunSuite with BeforeAndAfter with ScalaFutures {

  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))
  import Db.slickDriver.api._

  test("Adding a Team and a Player Works") {
    Db.database.run((Schema.leagues.schema ++ Schema.conferences.schema ++ Schema.divisions.schema ++ Schema.teams.schema ++ Schema.players.schema).create).futureValue

    val mlbId = Repo.createLeague(League(None, "MLB", "baseball", Nil))
    val nlId = Repo.createConference(Conference(None, "National League", mlbId.get, Nil))
    val alId = Repo.createConference(Conference(None, "American League", mlbId.get, Nil))
    val nlCentralId = Repo.createDivision(Division(None, "NL Central", nlId.get, Nil))
    val redsId = Repo.createTeam(Team(None, "Reds", "Cincinnati", nlCentralId.get, Nil, Nil))
    val frazier = Repo.createPlayer(Player(None, "Todd Frazier", 21, "3B", redsId.get))

    val mlb = Repo.getLeague(mlbId.get)

    println(mlb)

    val leagues = Repo.getLeagues()

    println(leagues)

    Repo.getLeagueOuterJoin(mlbId.get)

//    val redsId = Repo.createTeam(Team(None, "Reds", "Cincinnati")).get
//    val cubsId = Repo.createTeam(Team(None, "Cubs", "Chicago")).get
//    val teams = Repo.getTeams().get;
//
//    val reds = Repo.getTeam(redsId).get
//    val cubs = Repo.getTeam(cubsId).get
//
//    val frazierId = Repo.createPlayer(Player(None, "Todd Frazier", 21, "3B", Some(reds))).get
//    val frazier = Repo.getPlayer(frazierId).get
//
//    assert(teams.size == 2)
//    assert(reds == Team(Some(redsId), "Reds", "Cincinnati"))
//    assert(cubs == Team(Some(cubsId), "Cubs", "Chicago"))
//    assert(frazier == Player(Some(frazierId), "Todd Frazier", 21, "3B", Some(reds)))

  }

  //close the database when the test is over
  after(Db.database.close())

}
