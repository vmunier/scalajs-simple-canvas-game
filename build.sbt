
name := "SimpleGame"
version := "0.1-SNAPSHOT"
scalaVersion := "2.11.8"

enablePlugins(ScalaJSPlugin)

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.0",
  "com.lihaoyi" %%% "utest" % "0.4.3" % "test"
)

// needed for tests
jsDependencies += RuntimeDOM
testFrameworks += new TestFramework("utest.runner.Framework")

persistLauncher := true
persistLauncher in Test := false
