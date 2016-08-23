package nl.amsscala
package simplegame

import org.scalajs.dom
import org.scalajs.dom.ext.KeyCode.{ Down, Left, Right, Up }

import scala.collection.mutable
import scala.scalajs.js

/** The game with its rules. */
protected trait Game {
  private[this] val framesPerSec = 30

  /**
   * Initialize Game loop
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
      val updated = oldUpdated.getOrElse(new GameState(canvas, -1, true)).updateGame(delta / 1000, keysPressed, canvas)

      if (oldUpdated.isEmpty || (oldUpdated.get.hero.pos != updated.hero.pos))
        oldUpdated = SimpleCanvasGame.render(updated)

      prev = now
    }

    // Let's play this game!
    if (!headless) {
      // ToDo mobile application navigation
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
 * @param hero           Hero object with its position
 * @param monster        Monster object with its position
 * @param monstersCaught The score
 * @param newGame        Flags new game
 */
private case class GameState(
    hero: Hero[Int],
    monster: Monster[Int],
    monstersCaught: Int = 0,
    newGame: Boolean
) {

  /**
   * Update game objects according the pressed keys.
   *
   * @param latency  Passed time difference to adjust displacement.
   * @param keysDown Collection of key currently pressed.
   * @param canvas   The visual html element.
   * @return         Conditional updated GameState, not changed or start GameState.
   */
  def updateGame(latency: Double, keysDown: keysBufferType, canvas: dom.html.Canvas): GameState = {

    def directions = Map( // Key to direction translation
      Left -> Position(-1, 0), Right -> Position(1, 0), Up -> Position(0, -1), Down -> Position(0, 1)
    ).withDefaultValue(Position(0, 0))

    // Convert pressed keyboard keys to coordinates
    def displacements: mutable.Iterable[Position[Int]] = keysDown.map { case (k, _) => directions(k) }

    /* Experimental, does not properly work
    def displacements: mutable.Iterable[Position[Int]] = keysDown.map { case (key, (timeAtKPress, posAtKPress)) =>
        directions(key) * (Hero.speed * (now - timeAtKPress) / 1000 ).toInt + posAtKPress - hero.pos
    }

    val newHero = new Hero(displacements.fold(hero.pos)((z, x)=> z + x))
    */
    if (keysDown.isEmpty) this
    else {
      val newHero = new Hero(displacements.fold(hero.pos) { (z, i) => z + i * (Hero.speed * latency).toInt })

      if (newHero.pos.isValidPosition(Position(canvas.width, canvas.height), Hero.size)) // Are they touching?
        if (newHero.pos.areTouching(monster.pos, Hero.size)) // Reset the game when the player catches a monster
          new GameState(canvas, monstersCaught, true)
        else copy(hero = newHero, newGame = false)
      else this
    }
  }

  def isGameOver = newGame && monstersCaught != 0

  /**
   * Auxiliary GameState constructor
   *
   * Creates a start state, Hero centric and Monster random positions
   *
   * @param canvas   The visual html element
   * @param oldScore Score accumulator
   */
  def this(canvas: dom.html.Canvas, oldScore: Int, newGame: Boolean) {
    this(
      Hero(canvas.width / 2, canvas.height / 2),
      // Throw the monster somewhere on the screen randomly
      Monster((math.random * (canvas.width - Hero.size)).toInt, (math.random * (canvas.height - Hero.size)).toInt),
      oldScore + 1,
      newGame
    )
  }
}

/**
 * Monster class, holder for its coordinate, copied as extentension to the Hero class
 *
 * @param pos Monsters' position
 * @tparam T  Numeric generic abstraction
 */
private class Monster[T: Numeric](val pos: Position[T]) {
  override def equals(that: Any): Boolean = that match {
    case that: Monster[T] => this.pos == that.pos
    case _ => false
  }

  override def toString = s"${this.getClass.getSimpleName} $pos"
  protected[simplegame] def isValidPosition(canvas: dom.html.Canvas) =
    pos.isValidPosition(Position(canvas.width, canvas.height).asInstanceOf[Position[T]], Hero.size.asInstanceOf[T])
}

private object Monster {
  // def apply[T: Numeric](pos: Position[T]) = new Monster(pos)
  def apply[T: Numeric](x: T, y: T) = new Monster(Position(x, y))
}

private class Hero[A: Numeric](override val pos: Position[A]) extends Monster[A](pos)

/** Compagnion object of class Hero */
private object Hero {
  val size = 32
  val speed = 256

  // def apply[T: Numeric](pos: Position[T]) = new Hero(pos)
  def apply[T: Numeric](x: T, y: T) = new Hero(Position(x, y))
}
