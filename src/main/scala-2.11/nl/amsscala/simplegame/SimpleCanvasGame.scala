package nl.amsscala.simplegame

import org.scalajs.dom
import org.scalajs.dom.ext.KeyCode._

import scala.collection.mutable
import scala.scalajs.js

object SimpleCanvasGame extends js.JSApp with Page with Game {
  val directions = Map(Left -> Position(-1, 0), Right -> Position(1, 0), Up -> Position(0, -1), Down -> Position(0, 1))

  // Keyboard events store
  val keysDown = mutable.Map.empty[Int, Double]

  def main(): Unit = {
    // Create the canvas
    val canvas = dom.document.createElement("canvas").asInstanceOf[dom.html.Canvas]
    val ctx = canvas.getContext("2d")

    canvas.width = dom.window.innerWidth.toInt - 8
    canvas.height = dom.window.innerHeight.toInt - 38
    dom.document.body.appendChild(canvas)

    val (bgImage, heroImage, monsterImage) = (Image("img/background.png"), Image("img/hero.png"), Image("img/monster.png"))
    var (monstersCaught, hero, monster) = (0, new Hero(0, 0), new Monster(0, 0))

    // Reset the game when the player catches a monster
    def reset() = {
      hero = new Hero(Position(canvas.width / 2, canvas.height / 2))

      // Throw the monster somewhere on the screen randomly
      monster = new Monster(Position(
        Hero.size + (Math.random() * (canvas.width - 64)).toInt,
        Hero.size + (Math.random() * (canvas.height - 64)).toInt))
    }

    // Update game objects
    def update(modifier: Double) {
      val modif = (Hero.speed * modifier).toInt
      var Position(x, y) = hero.pos

      val newHero = new Hero(keysDown.map(k => directions(k._1)).fold(hero.pos) { (z, i) => z + i * modif } + hero.pos)
      if (newHero.isValidPosition(canvas)) {

        // Are they touching?
        if (hero.pos.areTouching(monster.pos, Hero.size)) {
          monstersCaught += 1
          reset()
        } else hero = newHero
      }
    }

    // Draw everything
    def render() {
      if (bgImage.isReady) ctx.drawImage(bgImage.element, 0, 0, canvas.width, canvas.height)
      if (heroImage.isReady) ctx.drawImage(heroImage.element, hero.pos.x, hero.pos.y)
      if (monsterImage.isReady) ctx.drawImage(monsterImage.element, monster.pos.x, monster.pos.y)

      // Score
      ctx.fillStyle = "rgb(250, 250, 250)"
      ctx.font = "24px Helvetica"
      ctx.textAlign = "left"
      ctx.textBaseline = "top"
      ctx.fillText("Goblins caught: " + monstersCaught, 32, 32)
    }

    // TODO Make this reactive
    var prev = js.Date.now()
    // The main game loop
    val gameLoop = () => {
      val now = js.Date.now()
      val delta = now - prev

      update(delta / 1000)
      render()

      prev = now
    }

    // Let's play this game!
    reset()

    dom.window.setInterval(gameLoop, 20)
  }

  dom.window.addEventListener("keydown", (e: dom.KeyboardEvent) =>
    e.keyCode match {
      case Left | Right | Up | Down => keysDown += e.keyCode -> js.Date.now()
      case _ =>
    }, useCapture = false)

  dom.window.addEventListener("keyup", (e: dom.KeyboardEvent) => {
    keysDown -= e.keyCode
  }, useCapture = false)

}
