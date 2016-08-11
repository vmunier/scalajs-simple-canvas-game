package nl.amsscala
package simplegame

import scala.scalajs.js.JSApp

object SimpleCanvasGame extends JSApp with Game with Page {
  def main(): Unit = play(canvas, headless = false)
}
