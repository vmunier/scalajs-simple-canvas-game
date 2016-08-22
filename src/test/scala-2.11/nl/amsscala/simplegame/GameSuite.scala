package nl.amsscala
package simplegame

import org.scalajs.dom
import org.scalajs.dom.ext.KeyCode.{ Down, Left, Right, Up }

import scala.collection.mutable

class GameSuite extends SuiteSpec {

  describe("A Hero") {
    describe("should tested within the limits") {
      val canvas = dom.document.createElement("canvas").asInstanceOf[dom.html.Canvas]
      canvas.width = 150
      canvas.height = 100
      it("good path") {
        Hero(0, 0).isValidPosition(canvas) shouldBe true
        Hero(150 - Hero.size, 100 - Hero.size).isValidPosition(canvas) shouldBe true
      }
      it("bad path") {
        Hero(-1, 0).isValidPosition(canvas) shouldBe false
        Hero(4, -1).isValidPosition(canvas) shouldBe false
        Hero(0, 101 - Hero.size).isValidPosition(canvas) shouldBe false
        Hero(151 - Hero.size, 0).isValidPosition(canvas) shouldBe false
      }

    }
  }

  describe("The Game") {
    describe("should tested by navigation keys") {
      import GameSuite.{ dummyTimeStamp, games }

      val canvas = dom.document.createElement("canvas").asInstanceOf[dom.html.Canvas]
      canvas.setAttribute("crossOrigin", "anonymous")
      canvas.width = 1242 // 1366
      canvas.height = 674 // 768

      val game = new GameState(canvas, -1).copy(monster = Monster(0, 0)) // Keep the monster out of site

      it("good path") {
        // No keys, no movement
        game.updateGame(1D, mutable.Map.empty, canvas) shouldBe game

        // Opposite horizontal navigation, no movement 1
        game.updateGame(1D, mutable.Map(Left -> dummyTimeStamp, Right -> dummyTimeStamp), canvas) shouldBe game

        // Opposite horizontal navigation, no movement 2
        game.updateGame(1D, mutable.Map(Right -> dummyTimeStamp, Left -> dummyTimeStamp), canvas) shouldBe game

        // Opposite vertical navigation, no movement 1
        game.updateGame(1D, mutable.Map(Up -> dummyTimeStamp, Down -> dummyTimeStamp), canvas) shouldBe game

        // Opposite vertical navigation, no movement 2
        game.updateGame(1D, mutable.Map(Down -> dummyTimeStamp, Up -> dummyTimeStamp), canvas) shouldBe game

        // All four directions, no movement
        game.updateGame(
          1D,
          mutable.Map(Up -> dummyTimeStamp, Right -> dummyTimeStamp, Left -> dummyTimeStamp, Down -> dummyTimeStamp),
          canvas
        ) shouldBe game

        games += game.updateGame(
          1D,
          mutable.Map(Left -> dummyTimeStamp, Right -> dummyTimeStamp, Up -> dummyTimeStamp, Down -> dummyTimeStamp),
          canvas
        )
        games.head shouldBe game
        games += game.copy(hero = new Hero(game.hero.pos - Position(Hero.speed, Hero.speed)))
        // North west navigation
        game.updateGame(1D, mutable.Map(Up -> dummyTimeStamp, Left -> dummyTimeStamp), canvas) shouldBe games.last

        games += game.copy(hero = new Hero(game.hero.pos + Position(Hero.speed, Hero.speed)))
        // South East navigation
        game.updateGame(1D, mutable.Map(Down -> dummyTimeStamp, Right -> dummyTimeStamp), canvas) shouldBe games.last

      }
      it("sad path") {
        // Illegal key code
        game.updateGame(1D, mutable.Map(0 -> dummyTimeStamp), canvas) shouldBe game
      }
      it("bad path") { // No move due a of out canvas limit case
        game.updateGame(1.48828125D, mutable.Map(Right -> dummyTimeStamp, Down -> dummyTimeStamp), canvas) shouldBe game
      }
      it("experiment") {

        println(games.mkString("\n"))

        val gs = new GameState(canvas, -1)
        canvas.setAttribute("crossOrigin", "anonymous")
        val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

        val y: scala.collection.mutable.Seq[Int] =
          ctx.getImageData(0, 0, canvas.width, canvas.height).data // .asInstanceOf[js.Array[Int]]

        println(s"Data, ${y.hashCode()}")
      }

    }
  }
}

object GameSuite {
  private val dummyTimeStamp = (0D, Position(0, 0))
  private val games = mutable.MutableList.empty[GameState]
}
