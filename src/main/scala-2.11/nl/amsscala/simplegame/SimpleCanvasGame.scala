package nl.amsscala
package simplegame

import scala.scalajs.js.JSApp

/**
 * Main entry point for application start
 */
object SimpleCanvasGame extends JSApp with Game with Page {
  /**
   * Entry point of execution
   * called as "nl.amsscala.simplegame.SimpleCanvasGame().main();"
   *
   * If `persistLauncher := true` set in sbt build file a `main-launcher.js` launcher is generated.
   */
  def main(): Unit = play(canvas, headless = false)
}
