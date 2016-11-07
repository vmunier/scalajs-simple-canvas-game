package nl.amsscala
package simplegame

import org.scalajs.dom

class CanvasComponentSuite extends SuiteSpec {
  describe("A Hero") {
    describe("should tested within the limits") {
      it("should be compared") {
        assert(new Hero[SimpleCanvasGame.T](Position(0, 0), null) === Hero(Position(0, 0)))
        assert(new Hero[SimpleCanvasGame.T](Position(1, 0), null) !== Hero(Position(0, 1)))
      }
      val canvas = dom.document.createElement("canvas").asInstanceOf[dom.html.Canvas]
      SimpleCanvasGame.resetCanvasWH(canvas, Position(150, 100))
      it("good path") {
        Hero(Position(0, 0)).isValidPosition(canvas) shouldBe true
        Hero(Position(150 - Hero.pxSize, 100 - Hero.pxSize)).isValidPosition(canvas) shouldBe true
      }
      it("bad path") {
        Hero(Position(-1, 0)).isValidPosition(canvas) shouldBe false
        Hero(Position(4, -1)).isValidPosition(canvas) shouldBe false
        Hero(Position(0, 101 - Hero.pxSize)).isValidPosition(canvas) shouldBe false
        Hero(Position(151 - Hero.pxSize, 0)).isValidPosition(canvas) shouldBe false
      }
    }
  }
}
