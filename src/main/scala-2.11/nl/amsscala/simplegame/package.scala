package nl.amsscala

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

    private def interSectsArea[P: Numeric](p0: Position[P], p1: Position[P], p2: Position[P], p3: Position[P]) = {
      @inline def intersectsWith(a0: P, b0: P, a1: P, b1: P) = a0 <= b1 && a1 <= b0

      intersectsWith(p0.x, p1.x, p2.x, p3.x) &&
        intersectsWith(p0.y, p1.y, p2.y, p3.y)
    }

    def isValidPosition(canvasPos: Position[P], size: P): Boolean = {
      // println(s"Testing: $x, $y")

      interSectsArea(Position(0, 0).asInstanceOf[Position[P]], canvasPos, this + size, this)
    }

    /**
     * Checks that two squares intersects
     *
     * @param posB Position of the second square
     * @param side size of both two squares
     * @return     True if a intersection occurs
     */
    def areTouching(posB: Position[P], side: P): Boolean = interSectsArea(this, this + side, posB, posB + side)
  }

}
