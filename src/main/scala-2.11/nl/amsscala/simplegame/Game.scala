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

  object Hero {
    val size = 32
    val speed = 256
  }


}
