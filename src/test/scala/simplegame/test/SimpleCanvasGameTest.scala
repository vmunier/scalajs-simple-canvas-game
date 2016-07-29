package simplegame

import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import utest._

object SimpleCanvasGameTest extends TestSuite {

  val tests = TestSuite {
    "isValidPosition" - {
      import SimpleCanvasGame._
      val canvas = dom.document.createElement("canvas").asInstanceOf[Canvas]

      assert(! isValidPosition(Position(-1, 0), canvas))
      assert(! isValidPosition(Position(4, -15), canvas))
      assert(isValidPosition(Position(0, 0), canvas))
    }
  }
}
