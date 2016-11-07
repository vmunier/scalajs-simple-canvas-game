package nl.amsscala
package simplegame

import org.scalajs.dom

import scala.collection.mutable

/**
 * Container holding the Game's state.
 * @param canvas         The visual HTML element
 * @param pageElements   This member lists the page elements. They are always in this order: Playground, Monster and Hero.
 *                       E.g. pageElements.head is Playground, pageElements(1) is the Monster, pageElements.takes(2) are those both.
 * @param monstersCaught
 * @param isNewGame      Flags game play is just fresh started
 * @param isGameOver     Flags a new turn
 * @param monstersHitTxt
 * @param _gameOverTxt
 * @param _explainTxt
 * @tparam T             Numeric generic abstraction
 */
class GameState[T: Numeric](canvas: dom.html.Canvas,
                            val pageElements: Vector[CanvasComponent[T]],
                            val isNewGame: Boolean = true,
                            val isGameOver: Boolean = false,
                            monstersCaught: Int = 0,
                            val monstersHitTxt: String = GameState.monsterText(0),
                            _gameOverTxt: => String = GameState.gameOverTxt,
                            _explainTxt: => String = GameState.explainTxt
                           ) {
  private[simplegame] def copy(hero: Hero[T]) = new GameState(canvas,
      pageElements.take(2) :+ hero,
      monstersCaught = monstersCaught,
      monstersHitTxt = monstersHitTxt,
      isNewGame = false)

  override def equals(that: Any): Boolean =
    that match {
      case that: GameState[T] =>  this.pageElements == that.pageElements
      case _ => false
    }

  def explainTxt = _explainTxt
  def gameOverTxt = _gameOverTxt
  def hero = pageElements.last.asInstanceOf[Hero[T]]
  private def monster = pageElements(1).asInstanceOf[Monster[T]]
  private def playGround = pageElements.head.asInstanceOf[Playground[T]]

  /**
   * Process on a regular basis the arrow keys pressed.
   *
   * @param latency
   * @param keysDown
   * @return a state with the Hero position adjusted.
   */
  def keyEffect(latency: Double, keysDown: mutable.Set[Int]): GameState[T] = {
    if (keysDown.isEmpty) this
    else {
      // Get new position according the pressed arrow keys
      val newHero = hero.keyEffect(latency, keysDown)
      // Are they touching?
      val size = Hero.pxSize.asInstanceOf[T]
      if (newHero.isValidPosition(canvas))
        if (newHero.pos.areTouching(monster.pos, size)) newGame // Reset the game when the player catches a monster
        else copy(hero = newHero) // New position for Hero, with isNewGame reset to false
      else this
    }
  }

  /**
   * New game, Monster randomized, Hero centralized, score updated
   * @return
   */
  private def newGame = new GameState(canvas,
    Vector(playGround, monster.copy(canvas), hero.copy(canvas)),
    monstersCaught = monstersCaught + 1,
    monstersHitTxt = GameState.monsterText(monstersCaught + 1),
    isGameOver = true)

  override def toString: String = s"${Position(canvas.width, canvas.height)} $pageElements isNew:$isNewGame $monstersHitTxt"

  require(pageElements.size == 3 &&
    playGround.isInstanceOf[Playground[T]] &&
    monster.isInstanceOf[Monster[T]] &&
    hero.isInstanceOf[Hero[T]], "Page elements are not listed well.")

}

/**
 * Companion object holding static constant definitions.
 */
object GameState {

  def apply[T: Numeric](canvas: dom.html.Canvas) =
    new GameState[T](canvas, Vector(new Playground[T](), Monster[T](canvas, Monster.randomPosition(canvas)), Hero[T](canvas)))

  // Randomness left out for testing
  def apply[T: Numeric](canvas: dom.html.Canvas, monsterPos : Position[T], heroPos : Position[T]) =
    new GameState[T](canvas, Vector(new Playground[T](), Monster[T](canvas, monsterPos), Hero(heroPos)))

  def explainTxt = "Use the arrow keys to\nattack the hidden monster."
  def gameOverTxt = "Game Over?"
  def monsterText(score: Int) = f"Goblins caught: $score%03d"
}
