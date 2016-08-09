name := "SimpleGame"
version := "0.0-SNAPSHOT"
organization := "nl.amsscala"
organizationName := "Amsterdam.scala Meetup Group"
organizationHomepage := Some(url("http://www.meetup.com/amsterdam-scala/"))
homepage := Some(url("http://github.com/amsterdam-scala/Sjs-Full-Window-HTML5-Canvas"))

startYear := Some(2016)

description := "Scala.js application using HTML5 canvas to fill the window, repainted on window resize."

licenses += "EUPL v.1.1" -> url("http://joinup.ec.europa.eu/community/eupl/og_page/european-union-public-licence-eupl-v11")

scalaVersion := "2.11.8"

// Keep this normalizedName constantly the same, otherwise the outputted JS filename will be changed.
normalizedName := "main"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.1",
  "com.lihaoyi" %%% "utest" % "0.4.3" % "test"
)

// needed for tests
jsDependencies += RuntimeDOM
testFrameworks += new TestFramework("utest.runner.Framework")

persistLauncher := true
persistLauncher in Test := false
