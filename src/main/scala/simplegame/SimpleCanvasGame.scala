package simplegame

import org.scalajs.dom
import org.scalajs.dom.ext.KeyCode

import scala.scalajs.js
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.HTMLImageElement

case class Position(x: Int, y: Int)

case class Monster(pos: Position)

case class Hero(
  pos: Position,
  speed: Int = 256 // movement in pixels per second
)

object Hero {
  val size = 32
}

class Image(src: String) {
  private var ready: Boolean = false

  val element = dom.document.createElement("img").asInstanceOf[HTMLImageElement]
  element.onload = (e: dom.Event) => ready = true
  element.src = src

  def isReady: Boolean = ready
}

object SimpleCanvasGame {

  def main(args: Array[String]): Unit = {
    initGame
  }

  def isValidPosition(pos: Position, canvas: Canvas): Boolean = {
    0 <= pos.x && (pos.x + Hero.size) <= canvas.width && 0 <= pos.y && (pos.y + Hero.size) <= canvas.height
  }

  def areTouching(posA: Position, posB: Position): Boolean = {
    posA.x <= (posB.x + Hero.size) && posB.x <= (posA.x + Hero.size) && posA.y <= (posB.y + Hero.size) && posB.y <= (posA.y + Hero.size)
  }

  def initGame(): Unit = {
    // Create the canvas
    val canvas = dom.document.createElement("canvas").asInstanceOf[Canvas]
    val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

    canvas.width = (0.95 * dom.window.innerWidth).toInt
    canvas.height = (0.95 * dom.window.innerHeight).toInt
    dom.document.body.appendChild(canvas)

    val bgImage = new Image("images/background.png")
    val heroImage = new Image("images/hero.png")
    val monsterImage = new Image("images/monster.png")

    var hero = Hero(Position(0, 0))
    var monster = Monster(Position(0, 0))

    var monstersCaught = 0

    // Handle keyboard controls
    import scala.collection.mutable.HashMap
    val keysDown = HashMap[Int, Boolean]()

    dom.window.addEventListener("keydown", (e: dom.KeyboardEvent) => {
      keysDown += e.keyCode -> true
    }, false)

    dom.window.addEventListener("keyup", (e: dom.KeyboardEvent) => {
      keysDown -= e.keyCode
    }, false)

    // Reset the game when the player catches a monster
    def reset() = {
      hero = hero.copy(pos = Position(canvas.width / 2, canvas.height / 2))

      // Throw the monster somewhere on the screen randomly
      monster = Monster(Position(
        Hero.size + (Math.random() * (canvas.width - 64)).toInt,
        Hero.size + (Math.random() * (canvas.height - 64)).toInt))
    }

    // Update game objects
    def update(modifier: Double) {
      val modif = (hero.speed * modifier).toInt
      var Position(x, y) = hero.pos
      if (keysDown.contains(KeyCode.Left))  x -= modif
      if (keysDown.contains(KeyCode.Right)) x += modif
      if (keysDown.contains(KeyCode.Up))    y -= modif
      if (keysDown.contains(KeyCode.Down))  y += modif

      val newPos = Position(x, y)
      if (isValidPosition(newPos, canvas)) {
        hero = hero.copy(pos = newPos)
      }

      // Are they touching?
      if (areTouching(hero.pos, monster.pos)) {
        monstersCaught += 1
        reset()
      }
    }

    // Draw everything
    def render() {
      if (bgImage.isReady) {
        ctx.drawImage(bgImage.element, 0, 0, canvas.width, canvas.height)
      }
      if (heroImage.isReady) {
        ctx.drawImage(heroImage.element, hero.pos.x, hero.pos.y)
      }
      if (monsterImage.isReady) {
        ctx.drawImage(monsterImage.element, monster.pos.x, monster.pos.y)
      }

      // Score
      ctx.fillStyle = "rgb(250, 250, 250)"
      ctx.font = "24px Helvetica"
      ctx.textAlign = "left"
      ctx.textBaseline = "top"
      ctx.fillText("Goblins caught: " + monstersCaught, 32, 32)
    }

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
}
