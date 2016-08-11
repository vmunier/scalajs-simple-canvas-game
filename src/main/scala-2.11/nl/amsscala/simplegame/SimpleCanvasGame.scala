package nl.amsscala.simplegame

import org.scalajs.dom

import scala.scalajs.js

object SimpleCanvasGame extends js.JSApp with Game with Page {

  def main(): Unit = {

    canvas.width = dom.window.innerWidth.toInt - 8
    canvas.height = dom.window.innerHeight.toInt - 38
    dom.document.body.appendChild(canvas)

    play(canvas)
  }

}
