package nl.amsscala
package simplegame

class GameStateSuite extends SuiteSpec {
  val game = GameState(null, Position(0, 0), Position(0, 0))

  describe("GameState") {
    describe("should perform functions") {
      it("should be compared") {
        assert(GameState(null, Position(0, 0), Position(0, 0)) == game)
        assert(GameState(null, Position(0, 0), Position(1, 0)) != game)
      }
    }
  }
}
