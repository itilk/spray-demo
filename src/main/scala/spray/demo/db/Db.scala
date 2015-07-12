package spray.demo.db

import slick.driver.H2Driver
import slick.driver.H2Driver.api._

/**
 * Created by itilk on 7/7/15.
 */
object Db {
  lazy val slickDriver = H2Driver
  lazy val database = Database.forConfig("h2mem1")
}
