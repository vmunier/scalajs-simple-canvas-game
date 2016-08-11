package nl.amsscala
package simplegame

import org.scalajs.dom
import org.scalajs.dom.ext.KeyCode._
import org.scalajs.dom.html

import scala.collection.mutable
import scala.scalajs.js

trait Game {
  // Keyboard events store
  val keysDown = mutable.Map.empty[Int, (Double, GameState)]
  var prev = js.Date.now()
  var oldUpdated: Option[GameState] = None

  def play(canvas : html.Canvas) {
    // The main game loop
    def gameLoop = () => {
      val now = js.Date.now()
      val delta = now - prev
      val updated = updater(oldUpdated.getOrElse(new GameState(canvas, -1)), delta / 1000).asInstanceOf[GameState]

      if (oldUpdated.isEmpty || (oldUpdated.get.hero.pos != updated.hero.pos)) {
        oldUpdated = SimpleCanvasGame.render(updated)
      }

      prev = now
    }

    // Let's play this game!
    dom.window.setInterval(gameLoop, 20)


    // Update game objects
    def updater(gs: GameState, modifier: Double): GameState = {
      def modif = (Hero.speed * modifier).toInt
      def directions = Map(Left -> Position(-1, 0), Right -> Position(1, 0), Up -> Position(0, -1), Down -> Position(0, 1))

      val newHero = new Hero(keysDown.map(k => directions(k._1)). // Convert pressed keyboard keys to coordinates
        fold(gs.hero.pos) { (z, i) => z + i * modif }) // Compute new position by adding and multiplying.
      if (newHero.isValidPosition(canvas))
      // Are they touching?
        if (newHero.pos.areTouching(gs.monster.pos, Hero.size)) // Reset the game when the player catches a monster
          new GameState(canvas, gs.monstersCaught)
        else gs.copy(hero = newHero)
      else gs
    }

    dom.window.addEventListener("keydown", (e: dom.KeyboardEvent) =>
      e.keyCode match {
        case Left | Right | Up | Down if oldUpdated.isDefined => keysDown += e.keyCode -> (js.Date.now(), oldUpdated.get)
        case _ =>
      }, useCapture = false)

    dom.window.addEventListener("keyup", (e: dom.KeyboardEvent) => {
      keysDown -= e.keyCode
    }, useCapture = false)
  }

}
