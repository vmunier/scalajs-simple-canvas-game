package nl.amsscala.simplegame

import org.scalajs.dom

import scala.Numeric.Implicits.infixNumericOps
import scala.collection.mutable
import scala.scalajs.js

case class Position[P: Numeric](x: P, y: P) {

  import Ordering.Implicits.infixOrderingOps

  def +(p: Position[P]) = Position(x + p.x, y + p.y)

  def isInTheCanvas(canvas: dom.html.Canvas, size: P): Boolean = {
    0.asInstanceOf[P] <= x &&
      (x + size) <= canvas.width.asInstanceOf[P] &&
      0.asInstanceOf[P] <= y &&
      (y + size) <= canvas.height.asInstanceOf[P]
  }

  def areTouching(posB: Position[P], size: P): Boolean = {
    x <= (posB.x + size) &&
      posB.x <= (x + size) &&
      y <= (posB.y + size) &&
      posB.y <= (y + size)
  }
}

class Monster[T: Numeric](val pos: Position[T]) {
  def this(x: T, y: T) = this(Position(x, y))
}

class Hero[A: Numeric](val pos: Position[A]) {
  def this(x: A, y: A) = this(Position(x, y))

  def isValidPosition(canvas: dom.html.Canvas): Boolean = pos.isInTheCanvas(canvas, Hero.size.asInstanceOf[A])
}

object Hero {
  val size = 32
  val speed = 256
}

class Image(src: String, var isReady: Boolean = false) {
  val element = dom.document.createElement("img").asInstanceOf[dom.raw.HTMLImageElement]

  element.onload = (e: dom.Event) => isReady = true
  element.src = src
}

object Image {
  def apply(src: String) = new Image(src)
}

object SimpleCanvasGame extends js.JSApp {

  // Keyboard events store
  val keysDown = mutable.Set.empty[Int]

  def main(): Unit = {
    // Create the canvas
    val canvas = dom.document.createElement("canvas").asInstanceOf[dom.html.Canvas]
    val ctx = canvas.getContext("2d")

    canvas.width = (0.95 * dom.window.innerWidth).toInt
    canvas.height = (0.95 * dom.window.innerHeight).toInt
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
      if (keysDown.contains(dom.ext.KeyCode.Left)) x -= modif
      if (keysDown.contains(dom.ext.KeyCode.Right)) x += modif
      if (keysDown.contains(dom.ext.KeyCode.Up)) y -= modif
      if (keysDown.contains(dom.ext.KeyCode.Down)) y += modif

      val newPos = new Hero(Position(x, y))
      if (newPos.isValidPosition(canvas)) hero = newPos

      // Are they touching?
      if (hero.pos.areTouching(monster.pos, Hero.size)) {
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

  dom.window.addEventListener("keydown", (e: dom.KeyboardEvent) => {
    keysDown += e.keyCode
  }, useCapture = false)

  dom.window.addEventListener("keyup", (e: dom.KeyboardEvent) => {
    keysDown -= e.keyCode
  }, useCapture = false)

}
