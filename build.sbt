name := "Simple Game"
version := "0.0-SNAPSHOT"

// ** Meta data **
description := "Simple HTML5 Canvas game ported to Scala.js."
startYear := Some(2016)
licenses += "EUPL v.1.1" -> url("http://joinup.ec.europa.eu/community/eupl/og_page/european-union-public-licence-eupl-v11")

organization := "nl.amsscala"
organizationName := "Amsterdam.scala Meetup Group"
organizationHomepage := Some(url("http://www.meetup.com/amsterdam-scala/"))
homepage := Some(url("http://github.com/amsterdam-scala/Sjs-Full-Window-HTML5-Canvas"))

// KEEP THIS normalizedName CONSTANTLY THE SAME, otherwise the outputted JS filename will be changed.
normalizedName := "main"


// ** Scala dependencies **
scalaVersion in ThisBuild := "2.11.8"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.1",
  "org.scalatest" %%% "scalatest" % "3.0.0" % "test",
  "com.lihaoyi" %%% "scalatags" % "0.6.0"
)

// ** Scala.js configuration **
// lazy val root = (project in file(".")).
enablePlugins(ScalaJSPlugin)
// needed for tests
jsDependencies += RuntimeDOM
//testFrameworks += new TestFramework("utest.runner.Framework")

// If true, a launcher script src="../[normalizedName]-launcher.js will be generated
// that always calls the main def indicated by the used JSApp trait.
persistLauncher := true
persistLauncher in Test := false

// Will create [normalizedName]-jsdeps.js containing all JavaScript libraries
// jsDependencies ++= Seq("org.webjars" % "jquery" % "3.1.0" / "3.1.0/jquery.js")
// jsDependencies += "org.webjars" % "bootstrap" % "3.3.6" / "bootstrap.js" minified "bootstrap.min.js" dependsOn "2.2.4/jquery.js"

// ScalaTest settings //
// testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oF")

// Workbench settings **
if (sys.env.isDefinedAt("CI")) {
  println("Workbench disabled", sys.env.getOrElse("CI", "?"))
  Seq.empty
} else {
  println("Workbench enabled")
  workbenchSettings
}

if (sys.env.isDefinedAt("CI")) normalizedName := normalizedName.value // Dummy
else refreshBrowsers <<= refreshBrowsers.triggeredBy(fastOptJS in Compile)

if (sys.env.isDefinedAt("CI")) normalizedName := normalizedName.value
else // Workbench has to know how to restart your application
  bootSnippet := "nl.amsscala.simplegame.SimpleCanvasGame().main();"
