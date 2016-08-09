name := "Simple Game"
version := "0.0-SNAPSHOT"

description := "Simple HTML5 Canvas game ported to Scala.js."
startYear := Some(2016)
licenses += "EUPL v.1.1" -> url("http://joinup.ec.europa.eu/community/eupl/og_page/european-union-public-licence-eupl-v11")

organization := "nl.amsscala"
organizationName := "Amsterdam.scala Meetup Group"
organizationHomepage := Some(url("http://www.meetup.com/amsterdam-scala/"))
homepage := Some(url("http://github.com/amsterdam-scala/Sjs-Full-Window-HTML5-Canvas"))

// KEEP THIS normalizedName CONSTANTLY THE SAME, otherwise the outputted JS filename will be changed.
normalizedName := "main"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.1",
  "com.lihaoyi" %%% "utest" % "0.4.3" % "test"
)

// needed for tests
jsDependencies += RuntimeDOM
testFrameworks += new TestFramework("utest.runner.Framework")
// lazy val root = (project in file(".")).
enablePlugins(ScalaJSPlugin)

// If true, a launcher script src="../[normalizedName]-launcher.js will
// be generated that always calls the main def indicated by the JSApp.
persistLauncher := true
persistLauncher in Test := false
