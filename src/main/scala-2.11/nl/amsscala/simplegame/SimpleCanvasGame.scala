package nl.amsscala.simplegame

import org.scalajs.dom
import org.scalajs.dom.ext.KeyCode._

import scala.collection.mutable
import scala.scalajs.js

object SimpleCanvasGame extends js.JSApp with Page with Game {

  // Keyboard events store
  val keysDown = mutable.Map.empty[Int, (Double, GameState)]
  // Create the canvas
  val canvas = dom.document.createElement("canvas").asInstanceOf[dom.html.Canvas]
  val ctx = canvas.getContext("2d")
  val (bgImage, heroImage, monsterImage) = (Image("img/background.png"), Image("img/hero.png"), Image("img/monster.png"))
  var prev = js.Date.now()
  var oldUpdated: Option[GameState] = None

  def main(): Unit = {

    canvas.width = dom.window.innerWidth.toInt - 8
    canvas.height = dom.window.innerHeight.toInt - 38
    dom.document.body.appendChild(canvas)

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

    // The main game loop
    def gameLoop = () => {
      val now = js.Date.now()
      val delta = now - prev
      val updated = updater(oldUpdated.getOrElse(new GameState(canvas, -1)), delta / 1000)

      if (oldUpdated.isEmpty || (oldUpdated.get.hero.pos != updated.hero.pos)) {
        oldUpdated = render(updated)
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
  }

  // Draw everything
  def render(gs: GameState) = {
    if (bgImage.isReady && heroImage.isReady && monsterImage.isReady) {
      ctx.drawImage(bgImage.element, 0, 0, canvas.width, canvas.height)
      ctx.drawImage(heroImage.element, gs.hero.pos.x, gs.hero.pos.y)
      ctx.drawImage(monsterImage.element, gs.monster.pos.x, gs.monster.pos.y)

      // Score
      ctx.fillStyle = "rgb(250, 250, 250)"
      ctx.font = "24px Helvetica"
      ctx.textAlign = "left"
      ctx.textBaseline = "top"
      ctx.fillText("Goblins caught: " + gs.monstersCaught, 32, 32)
      Some(gs)
    }
    else None
  }

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

}
