name := "spray-demo"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.0.0",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "io.spray" %% "spray-can" % "1.3.2",
  "io.spray" %% "spray-http" % "1.3.2",
  "io.spray" %% "spray-httpx" % "1.3.2",
  "io.spray" %% "spray-routing" % "1.3.2",
  "io.spray" %% "spray-json" % "1.3.2",
  "com.typesafe.akka" %% "akka-actor" % "2.3.12",
  "joda-time" % "joda-time" % "2.8.1",
  "com.h2database" % "h2" % "1.3.170",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)
    