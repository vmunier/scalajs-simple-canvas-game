package nl.amsscala.simplegame

import org.scalajs.dom
import org.scalajs.dom.ext.KeyCode._

import scala.collection.mutable
import scala.scalajs.js

object SimpleCanvasGame extends js.JSApp with Page with Game {

  // Keyboard events store
  val keysDown = mutable.Map.empty[Int, (Double, GameState)]

  def main(): Unit = {
    // Create the canvas
    val canvas = dom.document.createElement("canvas").asInstanceOf[dom.html.Canvas]
    val ctx = canvas.getContext("2d")

    canvas.width = dom.window.innerWidth.toInt - 8
    canvas.height = dom.window.innerHeight.toInt - 38
    dom.document.body.appendChild(canvas)

    val (bgImage, heroImage, monsterImage) = (Image("img/background.png"), Image("img/hero.png"), Image("img/monster.png"))
    var gameState = new GameState(canvas, -1)

    // Update game objects
    def update(modifier: Double): GameState = {
      def modif = (Hero.speed * modifier).toInt
      def directions = Map(Left -> Position(-1, 0), Right -> Position(1, 0), Up -> Position(0, -1), Down -> Position(0, 1))

      val newHero = new Hero(keysDown.map(k => directions(k._1)). // Convert pressed keyboard keys to coordinates
        fold(gameState.hero.pos) { (z, i) => z + i * modif }) // Compute new position by adding and multiplying.
      if (newHero.isValidPosition(canvas))
      // Are they touching?
        if (newHero.pos.areTouching(gameState.monster.pos, Hero.size)) // Reset the game when the player catches a monster
          gameState = new GameState(canvas, gameState.monstersCaught)
        else gameState.hero = newHero

      gameState
    }

    // Draw everything
    def render(gs: GameState) {
      if (bgImage.isReady) ctx.drawImage(bgImage.element, 0, 0, canvas.width, canvas.height)
      if (heroImage.isReady) ctx.drawImage(heroImage.element, gs.hero.pos.x, gs.hero.pos.y)
      if (monsterImage.isReady) ctx.drawImage(monsterImage.element, gs.monster.pos.x, gs.monster.pos.y)

      // Score
      ctx.fillStyle = "rgb(250, 250, 250)"
      ctx.font = "24px Helvetica"
      ctx.textAlign = "left"
      ctx.textBaseline = "top"
      ctx.fillText("Goblins caught: " + gs.monstersCaught, 32, 32)
    }

    var prev = js.Date.now()
    var oldUpdated : Option[GameState] = None
    // The main game loop
    val gameLoop = () => {
      val now = js.Date.now()
      val delta = now - prev
      val updated = update(delta / 1000)
      render(gameState)

      prev = now
    }

    // Let's play this game!
    dom.window.setInterval(gameLoop, 20)

    dom.window.addEventListener("keydown", (e: dom.KeyboardEvent) =>
      e.keyCode match {
        case Left | Right | Up | Down => keysDown += e.keyCode -> (js.Date.now(), gameState)
        case _ =>
      }, useCapture = false)

    dom.window.addEventListener("keyup", (e: dom.KeyboardEvent) => {
      keysDown -= e.keyCode
    }, useCapture = false)
  }

}
