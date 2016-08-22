package nl.amsscala

import org.scalajs.dom

/**
 * Provides generic classes and operators for dealing with 2D positions.
 *
 */
package object simplegame {
  // Experimental timestamp and position, displacement is a function of time
  type keysBufferType = scala.collection.mutable.Map[Int, (Double, Position[Int])]

  case class Position[P: Numeric](x: P, y: P) {

    import Numeric.Implicits.infixNumericOps
    import Ordering.Implicits.infixOrderingOps

    def +(p: Position[P]) = Position(x + p.x, y + p.y)

    def +(term: P) = Position(x + term, y + term)

    def -(p: Position[P]) = Position(x - p.x, y - p.y)

    def *(p: Position[P]) = Position(x * p.x, y * p.y)

    def *(factor: P) = Position(x * factor, y * factor)

    @inline def intersectsWith(a0: P, b0: P, a1: P, b1: P) = a0 <= b1 && a1 <= b0
    def isValidPosition(canvasPos: Position[P], correctedThis: Position[P]): Boolean = {
      // println(s"Testing: $x, $y")

      intersectsWith(0.asInstanceOf[P], canvasPos.x, correctedThis.x, x) &&
        intersectsWith(0.asInstanceOf[P], canvasPos.y, correctedThis.y, y)
    }

    def areTouching(posB: Position[P], correctedThis: Position[P], correctedPosB: Position[P]): Boolean = {
      intersectsWith(x, correctedThis.x, posB.x, correctedPosB.x) &&
        intersectsWith(y, correctedThis.y, posB.y, correctedPosB.y)
    }
  }
}
