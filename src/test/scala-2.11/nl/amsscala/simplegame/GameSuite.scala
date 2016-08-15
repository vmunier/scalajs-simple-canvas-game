package nl.amsscala
package simplegame

import org.scalajs.dom

class GameSuite extends SuiteSpec with Game {

  describe("A Hero") {
    describe("should tested if it within the limits") {
      val canvas = dom.document.createElement("canvas").asInstanceOf[dom.html.Canvas]
      it("bad path") {

        assert(!new Hero(-1, 0).isValidPosition(canvas))
        assert(!new Hero(4, -15).isValidPosition(canvas))
      }
      it("good path") {
        assert(new Hero(0, 0).isValidPosition(canvas))
      }

    }
  }
}
