lazy val commonSettings = Seq(
                name := "Simple HTML5 Canvas Game",
             version := "2.0",
         description := "Simple HTML5 Canvas game ported to Scala.js.",
        organization := "nl.amsscala",
    organizationName := "Amsterdam.scala Meetup Group",
organizationHomepage := Some(url("http://www.meetup.com/amsterdam-scala/")),
            homepage := Some(url("http://github.com/amsterdam-scala/Sjs-Full-Window-HTML5-Canvas")),
           startYear := Some(2016),
            licenses += "EUPL-1.1" -> url("http://joinup.ec.europa.eu/community/eupl/og_page/european-union-public-licence-eupl-v11")
)
// KEEP THIS normalizedName CONSTANTLY THE SAME, otherwise the outputted JS filename will be changed.
      normalizedName := "main"

// ** Scala dependencies **
scalaVersion in ThisBuild := "2.12.0"
scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")
scalacOptions in (Compile,doc) ++=
  Seq("-doc-root-content", baseDirectory.value + "/src/main/scala-2.12/root-doc.md", "-groups", "-implicits")

libraryDependencies ++= Seq(
//"be.doeraene"        %%% "scalajs-jquery" % "0.9.0",
  "com.lihaoyi"        %%% "scalatags"      % "0.6.2",
  "org.scala-js"       %%% "scalajs-dom"    % "0.9.1",
  "org.scalatest"      %%% "scalatest"      % "3.0.0" % "test"
)
skip in packageJSDependencies := false // All JavaScript dependencies to be concatenated to a single file

scalacOptions in (Compile,doc) ++= Seq("-doc-root-content", baseDirectory.value + "/src/main/scala-2.12/root-doc.md",
  "-groups", "-implicits")

// ** Scala.js configuration **

lazy val root: Project = (project in file(".")).enablePlugins(ScalaJSPlugin).settings(commonSettings: _*).
  configure(InBrowserTesting.js)

// jsEnv in Test := new org.scalajs.jsenv.selenium.SeleniumJSEnv(org.scalajs.jsenv.selenium.Chrome())

// Firefox works only with FireFox 46.0.1-, and since 48.0 GeckoDriver (aka Marionette)
// (https://ftp.mozilla.org/pub/firefox/releases/46.0.1/win64-EME-free/en-US/Firefox%20Setup%2046.0.1.exe)
// jsEnv in Test := new org.scalajs.jsenv.selenium.SeleniumJSEnv(org.scalajs.jsenv.selenium.Firefox())

// If true, a launcher script src="../[normalizedName]-launcher.js will be generated
// that always calls the main def indicated by the used JSApp trait.
persistLauncher in Compile := true
persistLauncher in Test := false

// Will create [normalizedName]-jsdeps.js containing all JavaScript libraries
// jsDependencies ++= Seq("org.webjars" % "jquery" % "2.1.4" / "2.1.4/jquery.js")
// jsDependencies += "org.webjars" % "bootstrap" % "3.3.6" / "bootstrap.js" minified "bootstrap.min.js" dependsOn "2.2.4/jquery.js"

// ScalaTest settings //
// testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oF")

// Li Haoyi's Workbench settings **
if (sys.env.isDefinedAt("CI")) {
  println("[Info] Li Haoyi's workbench disabled ", sys.env.getOrElse("CI", "?"))
  Seq.empty
} else workbenchSettings

if (sys.env.isDefinedAt("CI")) normalizedName := normalizedName.value // Dummy
else // Update without refreshing the page every time fastOptJS completes
  refreshBrowsers <<= refreshBrowsers.triggeredBy(fastOptJS in Compile)

if (sys.env.isDefinedAt("CI")) normalizedName := normalizedName.value
else // Workbench has to know how to restart your application
  bootSnippet := "nl.amsscala.simplegame.SimpleCanvasGame().main();"
