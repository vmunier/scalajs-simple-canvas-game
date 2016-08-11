package nl.amsscala
package simplegame

import org.scalajs.dom

case class GameState(hero: Hero[Int],
                     monster: Monster[Int],
                     monstersCaught: Int = 0) {
  def this(canvas: dom.html.Canvas, oldScore: Int) =
    this(new Hero(Position(canvas.width / 2, canvas.height / 2)),
      // Throw the monster somewhere on the screen randomly
      new Monster(Position(
        Hero.size + (math.random * (canvas.width - 64)).toInt,
        Hero.size + (math.random * (canvas.height - 64)).toInt)),
      oldScore + 1)
}

class Monster[T: Numeric](val pos: Position[T]) {
  def this(x: T, y: T) = this(Position(x, y))
}

case class Position[P: Numeric](x: P, y: P) {

  import Numeric.Implicits.infixNumericOps
  import Ordering.Implicits.infixOrderingOps

  def +(p: Position[P]) = Position(x + p.x, y + p.y)

  def *(factor: P) = Position(x * factor, y * factor)

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


class Hero[A: Numeric](val pos: Position[A]) {
  def this(x: A, y: A) = this(Position(x, y))

  def isValidPosition(canvas: dom.html.Canvas): Boolean = pos.isInTheCanvas(canvas, Hero.size.asInstanceOf[A])
}

object Hero {
  val size = 32
  val speed = 256
}
