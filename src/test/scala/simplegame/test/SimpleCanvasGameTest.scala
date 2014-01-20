package simplegame
package test

import scala.scalajs.js
import js.Dynamic.{ global => g }
import scala.scalajs.test.JasmineTest
import org.scalajs.dom.extensions._
import org.scalajs.dom

object SimpleCanvasGameTest extends JasmineTest {

  describe("SimpleCanvasGame") {
    it("should implement isValidPosition()") {
      import SimpleCanvasGame._
      val canvas = dom.document.createElement("canvas").cast[dom.HTMLCanvasElement]

      expect(isValidPosition(Position(-1, 0), canvas)).toBe(false)
      expect(isValidPosition(Position(4, -15), canvas)).toBe(false)
      expect(isValidPosition(Position(0, 0), canvas)).toBe(true)
    }
  }
}
