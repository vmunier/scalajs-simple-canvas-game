package nl.amsscala
package simplegame

import org.scalajs.dom
import org.scalajs.dom.ext.KeyCode.{Down, Left, Right, Up}

import scala.collection.mutable
import scala.scalajs.js

protected trait Game {
  private val framesPerSec = 30

  /**
    *
    * @param canvas   The visual html element
    * @param headless An option to run for testing
    */
  protected def play(canvas: dom.html.Canvas, headless: Boolean) {
    // Keyboard events store
    val keysPressed: keysBufferType = mutable.Map.empty
    var prev = 0D
    var oldUpdated: Option[GameState] = None

    // The main game loop
    def gameLoop = () => {
      val now = js.Date.now()
      val delta = now - prev
      val updated = oldUpdated.getOrElse(new GameState(canvas, -1)).updateGame(delta / 1000, keysPressed, canvas)

      if (oldUpdated.isEmpty || (oldUpdated.get.hero.pos != updated.hero.pos))
        oldUpdated = SimpleCanvasGame.render(updated)

      prev = now
    }

    // Let's play this game!
    if (!headless) {
      dom.window.setInterval(gameLoop, 1000 / framesPerSec)

      dom.window.addEventListener("keydown", (e: dom.KeyboardEvent) =>
        e.keyCode match {
          case Left | Right | Up | Down if oldUpdated.isDefined =>
            keysPressed += e.keyCode -> (js.Date.now(), oldUpdated.get.hero.pos)
          case _ =>
        }, useCapture = false)

      dom.window.addEventListener("keyup", (e: dom.KeyboardEvent) => {
        keysPressed -= e.keyCode
      }, useCapture = false)
    }
  }
}

/**
  * GameState constructor
  *
  * @param hero
  * @param monster
  * @param monstersCaught
  */
protected case class GameState(hero: Hero[Int], monster: Monster[Int], monstersCaught: Int = 0) {

  /**
    * Update game objects
    *
    * @param modifier
    * @param keysDown
    * @param canvas The visual html element
    * @return Updated GameState
    */
  protected[simplegame] def updateGame(modifier: Double, keysDown: keysBufferType, canvas: dom.html.Canvas): GameState = {

    def directions = Map(// Key to direction translation
      Left -> Position(-1, 0), Right -> Position(1, 0), Up -> Position(0, -1), Down -> Position(0, 1)).
      withDefaultValue(Position(0, 0))

    // Convert pressed keyboard keys to coordinates
    def displacements: mutable.Iterable[Position[Int]] = keysDown.map { case (k, _) => directions(k) }

    /* Experimental, does not properly work
    def displacements: mutable.Iterable[Position[Int]] = keysDown.map { case (key, (timeAtKPress, posAtKPress)) =>
        directions(key) * (Hero.speed * (now - timeAtKPress) / 1000 ).toInt + posAtKPress - hero.pos
    }

    val newHero = new Hero(displacements.fold(hero.pos)((z, x)=> z + x))
    */

    val newHero = new Hero(displacements.fold(hero.pos) { (z, i) => z + i * (Hero.speed * modifier).toInt })

    if (newHero.isValidPosition(canvas)) // Are they touching?
      if (newHero.pos.areTouching(monster.pos, Hero.size)) // Reset the game when the player catches a monster
        new GameState(canvas, monstersCaught)
      else copy(hero = newHero)
    else this
  }

  /**
    * Auxiliary constructor
    *
    * @param canvas   The visual html element
    * @param oldScore Score accumulator
    */
  protected[simplegame] def this(canvas: dom.html.Canvas, oldScore: Int) =
  this(
    new Hero(Position(canvas.width / 2, canvas.height / 2)),
    // Throw the monster somewhere on the screen randomly
    new Monster(Position(
      Hero.size + (math.random * (canvas.width - 64)).toInt,
      Hero.size + (math.random * (canvas.height - 64)).toInt
    )),
    oldScore + 1
  )
}

protected class Monster[T: Numeric](val pos: Position[T]) {
  def isValidPosition(canvas: dom.html.Canvas): Boolean = pos.isWithinTheCanvas(canvas, Hero.size.asInstanceOf[T])

  override def equals(that: Any): Boolean = that match {
    case that: Monster[T] => this.pos == that.pos
    case _ => false
  }

  override def toString = s"${this.getClass.getSimpleName} $pos"
}

protected class Hero[A: Numeric](override val pos: Position[A]) extends Monster[A](pos) {
  /**
    * Auxiliary constructor
    *
    * @param x
    * @param y
    */
  def this(x: A, y: A) = this(Position(x, y))

}

private object Hero {
  val size = 32
  val speed = 256
}
