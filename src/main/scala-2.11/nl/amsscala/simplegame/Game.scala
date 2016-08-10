package nl.amsscala
package simplegame

import org.scalajs.dom

trait Game {

  class Monster[T: Numeric](val pos: Position[T]) {
    def this(x: T, y: T) = this(Position(x, y))
  }

  class Hero[A: Numeric](val pos: Position[A]) {
    def this(x: A, y: A) = this(Position(x, y))

    def isValidPosition(canvas: dom.html.Canvas): Boolean = pos.isInTheCanvas(canvas, Hero.size.asInstanceOf[A])
  }

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

  object Hero {
    val size = 32
    val speed = 256
  }

}
