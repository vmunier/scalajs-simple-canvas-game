package nl.amsscala

import org.scalajs.dom

package object simplegame {

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

}
