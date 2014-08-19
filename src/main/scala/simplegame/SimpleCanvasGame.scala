package simplegame

import scala.scalajs.js
import scala.scalajs.js.JSApp
import js.Dynamic.{ global => g }
import org.scalajs.dom.extensions._
import org.scalajs.dom

class HTMLImageElement extends dom.HTMLImageElement {
  var onload: js.Function1[dom.Event, _] = ???
}

case class Position(x: js.Number, y: js.Number)
case class Hero(
  pos: Position,
  speed: js.Number = 256 // movement in pixels per second
)

case class Monster(pos: Position)

object SimpleCanvasGame extends JSApp{
  def main(): Unit = {
    initGame
  }

  def isValidPosition(pos: Position, canvas: dom.HTMLCanvasElement): Boolean = {
    0 <= pos.x && (pos.x + 32) <= canvas.width && 0 <= pos.y && (pos.y + 32) <= canvas.height
  }

  def areTouching(posA: Position, posB: Position): Boolean = {
    posA.x <= (posB.x + 32) && posB.x <= (posA.x + 32) && posA.y <= (posB.y + 32) && posB.y <= (posA.y + 32)
  }

  def initGame {
    val i=1
    // Create the canvas
    val canvas = dom.document.createElement("canvas").cast[dom.HTMLCanvasElement]
    val ctx = canvas.getContext("2d").cast[dom.CanvasRenderingContext2D]

    canvas.width = (0.95 * g.window.innerWidth).intValue
    canvas.height = (0.95 * g.window.innerHeight).intValue
    g.document.body.appendChild(canvas)

    // Background image
    var bgReady = false;
    val bgImage = dom.document.createElement("img").asInstanceOf[HTMLImageElement]
    bgImage.onload = (e: dom.Event) => {
      bgReady = true
    }
    bgImage.src = "images/background.png"

    // Hero image
    var heroReady = false
    val heroImage = dom.document.createElement("img").asInstanceOf[HTMLImageElement]
    heroImage.onload = (e: dom.Event) => {
      heroReady = true
    }
    heroImage.src = "images/hero.png"

    // Monster image
    var monsterReady = false
    val monsterImage = dom.document.createElement("img").asInstanceOf[HTMLImageElement]
    monsterImage.onload = (e: dom.Event) => {
      monsterReady = true
    }
    monsterImage.src = "images/monster.png"

    var hero = Hero(Position(0, 0))
    var monster = Monster(Position(0, 0))

    var monstersCaught = 0

    // Handle keyboard controls
    import scala.collection.mutable.HashMap
    val keysDown = HashMap[Int, Boolean]()

    g.addEventListener("keydown", (e: dom.KeyboardEvent) => {
      keysDown += e.keyCode -> true
    }, false)

    g.addEventListener("keyup", (e: dom.KeyboardEvent) => {
      keysDown -= e.keyCode
    }, false)

    // Reset the game when the player catches a monster
    def reset() = {
      hero = hero.copy(pos = Position(canvas.width / 2, canvas.height / 2))

      // Throw the monster somewhere on the screen randomly
      monster = Monster(Position(
        32 + (Math.random() * (canvas.width - 64)),
        32 + (Math.random() * (canvas.height - 64))))
    }

    // Update game objects
    def update(modifier: js.Number) {
      val modif = hero.speed * modifier
      var Position(x, y) = hero.pos
      if (keysDown.contains(37)) { // Player holding left
        x -= modif
      }
      if (keysDown.contains(38)) { // Player holding up
        y -= modif
      }
      if (keysDown.contains(39)) { // Player holding right
        x += modif
      }
      if (keysDown.contains(40)) { // Player holding down
        y += modif
      }
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
      if (bgReady) {
        ctx.drawImage(bgImage, 0, 0, canvas.width, canvas.height)
      }
      if (heroReady) {
        ctx.drawImage(heroImage, hero.pos.x, hero.pos.y)
      }
      if (monsterReady) {
        ctx.drawImage(monsterImage, monster.pos.x, monster.pos.y)
      }

      // Score
      ctx.fillStyle = "rgb(250, 250, 250)"
      ctx.font = "24px Helvetica"
      ctx.textAlign = "left"
      ctx.textBaseline = "top"
      ctx.fillText("Goblins caught: " + monstersCaught, 32, 32)
    }

    var then = js.Date.now()
    // The main game loop
    val gameLoop = () => {
      val now = js.Date.now()
      val delta = now - then

      update(delta / 1000)
      render()

      then = now
    }

    // Let's play this game!
    reset()

    g.setInterval(gameLoop, 50) // Execute as fast as possible
  }
}
