package nl.amsscala
package simplegame

import nl.amsscala.simplegame.SimpleCanvasGame.GameState
import nl.amsscala.simplegame.SimpleCanvasGame.GameState._
import org.scalajs.dom
import org.scalajs.dom.ext.KeyCode._

import scala.collection.mutable
import scala.scalajs.js
import SimpleCanvasGame.Hero

trait Game /*extends Page*/ {
  // Keyboard events store
  val keysDown = mutable.Map.empty[Int, (Double, GameState)]
  var prev = js.Date.now()
  var oldUpdated: Option[GameState] = None

  class Hero[A: Numeric](val pos: Position[A]) {
    def this(x: A, y: A) = this(Position(x, y))

    def isValidPosition(canvas: dom.html.Canvas): Boolean = pos.isInTheCanvas(canvas, Hero.size.asInstanceOf[A])
  }

  // Update game objects
  def updater(gs: GameState, modifier: Double): GameState = {
    def modif = (Hero.speed * modifier).toInt
    def directions = Map(Left -> Position(-1, 0), Right -> Position(1, 0), Up -> Position(0, -1), Down -> Position(0, 1))

    val newHero : SimpleCanvasGame.Hero[Int] = new SimpleCanvasGame.Hero(keysDown.map(k => directions(k._1)). // Convert pressed keyboard keys to coordinates
      fold(gs.hero.pos) { (z, i) => z + i * modif }) // Compute new position by adding and multiplying.
    if (newHero.isValidPosition(SimpleCanvasGame.canvas))
    // Are they touching?
      if (newHero.pos.areTouching(gs.monster.pos, Hero.size)) // Reset the game when the player catches a monster
        new GameState(SimpleCanvasGame.canvas, gs.monstersCaught)
      else gs.copy(hero = newHero)
    else gs
  }


  // The main game loop
  def gameLoop = () => {
    val now = js.Date.now()
    val delta = now - prev
    val updated = updater(oldUpdated.getOrElse(new GameState(SimpleCanvasGame.canvas, -1)), delta / 1000)

    if (oldUpdated.isEmpty || (oldUpdated.get.hero.pos != updated.hero.pos)) {
      oldUpdated = SimpleCanvasGame.render(updated)
    }

    prev = now
  }

  // Let's play this game!
  dom.window.setInterval(gameLoop, 20)

  dom.window.addEventListener("keydown", (e: dom.KeyboardEvent) =>
    e.keyCode match {
      case Left | Right | Up | Down if oldUpdated.isDefined => keysDown += e.keyCode -> (js.Date.now(), oldUpdated.get)
      case _ =>
    }, useCapture = false)

  dom.window.addEventListener("keyup", (e: dom.KeyboardEvent) => {
    keysDown -= e.keyCode
  }, useCapture = false)




class Monster[T: Numeric](val pos: Position[T]) {
    def this(x: T, y: T) = this(Position(x, y))
  }

  object Hero {
    val size = 32
    val speed = 256
  }

}
