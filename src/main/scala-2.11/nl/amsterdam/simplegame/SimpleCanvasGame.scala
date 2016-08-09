package nl.amsterdam
package simplegame

import org.scalajs.dom
import org.scalajs.dom.ext.KeyCode
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.HTMLImageElement

import scala.collection.mutable
import scala.scalajs.js

case class Position(x: Int, y: Int)

class Monster(val pos: Position)

class Hero(val pos: Position)

object Hero {
  val size = 32
  val speed =256
}

class Image(src: String) {
  val element = dom.document.createElement("img").asInstanceOf[HTMLImageElement]
  private var ready: Boolean = false
  element.onload = (e: dom.Event) => ready = true
  element.src = src

  def isReady: Boolean = ready
}

object SimpleCanvasGame extends js.JSApp {

  def main(): Unit = {
    // Create the canvas
    val canvas = dom.document.createElement("canvas").asInstanceOf[Canvas]
    val ctx = canvas.getContext("2d") //.asInstanceOf[dom.CanvasRenderingContext2D]

    canvas.width = (0.95 * dom.window.innerWidth).toInt
    canvas.height = (0.95 * dom.window.innerHeight).toInt
    dom.document.body.appendChild(canvas)

    val bgImage = new Image("img/background.png")
    val heroImage = new Image("img/hero.png")
    val monsterImage = new Image("img/monster.png")

    var hero = new Hero(Position(0, 0))
    var monster = new Monster(Position(0, 0))

    var monstersCaught = 0

    // Handle keyboard controls
    val keysDown = mutable.HashMap[Int, Boolean]()

    dom.window.addEventListener("keydown", (e: dom.KeyboardEvent) => {
      keysDown += e.keyCode -> true
    }, useCapture = false)

    dom.window.addEventListener("keyup", (e: dom.KeyboardEvent) => {
      keysDown -= e.keyCode
    }, useCapture = false)

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
      if (keysDown.contains(KeyCode.Left)) x -= modif
      if (keysDown.contains(KeyCode.Right)) x += modif
      if (keysDown.contains(KeyCode.Up)) y -= modif
      if (keysDown.contains(KeyCode.Down)) y += modif

      val newPos = Position(x, y)
      if (isValidPosition(newPos, canvas)) hero = new Hero(newPos)

      // Are they touching?
      if (areTouching(hero.pos, monster.pos)) {
        monstersCaught += 1
        reset()
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

    dom.window.setInterval(gameLoop, 1) // Execute as fast as possible
  }

  def isValidPosition(pos: Position, canvas: Canvas): Boolean = {
    0 <= pos.x && (pos.x + Hero.size) <= canvas.width && 0 <= pos.y && (pos.y + Hero.size) <= canvas.height
  }

  def areTouching(posA: Position, posB: Position): Boolean = {
    posA.x <= (posB.x + Hero.size) && posB.x <= (posA.x + Hero.size) && posA.y <= (posB.y + Hero.size) && posB.y <= (posA.y + Hero.size)
  }
}
