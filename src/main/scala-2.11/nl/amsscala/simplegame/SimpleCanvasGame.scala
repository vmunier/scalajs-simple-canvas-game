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

  case class GameState(hero: Hero[Int],
                       monster: Monster[Int],
                       monstersCaught: Int = 0) {
    def this(canvas: dom.html.Canvas, oldScore: Int) =
      this(new Hero(Position(canvas.width / 2, canvas.height / 2)),
        // Throw the monster somewhere on the screen randomly
        new Monster(Position(
          Hero.size + (math.random * (canvas.width - 64)).toInt,
          Hero.size + (math.random * (canvas.height - 64)).toInt)),
        oldScore + 1)
  }
}
