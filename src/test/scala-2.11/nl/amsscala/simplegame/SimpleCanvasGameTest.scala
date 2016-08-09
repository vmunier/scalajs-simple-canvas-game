package nl.amsscala
package simplegame

import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import utest.{TestSuite, TestableString}

object SimpleCanvasGameTest extends TestSuite {

  val tests = TestSuite {
    "isValidPosition" - {
      val canvas = dom.document.createElement("canvas").asInstanceOf[Canvas]

      assert(!(new Hero(Position(-1, 0)).isValidPosition(canvas)))
      assert(!(new Hero(Position(4, -15)).isValidPosition(canvas)))
      assert((new Hero(Position(0, 0)).isValidPosition(canvas)))
    }
  }
}
