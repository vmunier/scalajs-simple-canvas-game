package nl.amsscala
package simplegame

import org.scalatest.AsyncFlatSpec

import scala.collection.mutable
import scala.concurrent.Future

class PageSuite extends AsyncFlatSpec with Page {
  // All graphical features are placed just outside the playground
  lazy val gameState = GameState[SimpleCanvasGame.T](canvas, doubleInitialLUnder, doubleInitialLUnder)
  lazy val loaders = gameState.pageElements.map(pg =>
    imageFuture((if (Seq(gameState.pageElements.head, gameState.pageElements.last).contains(pg)) urlBase1 else urlBase1) + pg.src))
  // Collect all Futures of onload events
  val urlBase0 = "http://lambdalloyd.net23.net/SimpleGame/views/"
  val urlBase1 = "https://amsterdam-scala.github.io/Sjs-Simple-HTML5-canvas-game/public/views/"
  val initialLUnder = Position(512, 480).asInstanceOf[Position[SimpleCanvasGame.T]]
  val doubleInitialLUnder = initialLUnder + initialLUnder

  implicit override def executionContext = scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  def context2Hashcode[C: Numeric](size: Position[C]) = {
    val UintClampedArray: mutable.Seq[Int] =
      ctx.getImageData(0, 0, size.x.asInstanceOf[Int], size.y.asInstanceOf[Int]).data
    UintClampedArray.hashCode()
  }

  def expectedHashCode = Map("background.png" -> 1425165765, "monster.png" -> -277415456, "hero.png" -> -731024817)
  //def expectedHashCode = Map("background.png" -> -1768009948, "monster.png" -> 1817836310, "hero.png" -> 1495155181)
  def getImgName(url: String) = url.split('/').last

  def testHarness(gs: GameState[SimpleCanvasGame.T], text: String, assertion: () => Boolean) = {
    render(gs)
    info(text)
    assert(assertion(), s"Thrown probably by value ${context2Hashcode(doubleInitialLUnder)}")
  }

  // ***** Test the navigation of the Hero character graphical
  def navigateHero(gs: GameState[SimpleCanvasGame.T], move: Position[Int]) =
  gs.copy(new Hero(initialLUnder + move.asInstanceOf[Position[SimpleCanvasGame.T]], gs.pageElements.last.img))

  // Don't rely on the browsers defaults
  resetCanvasWH(canvas, initialLUnder)

  // You can map assertions onto a Future, then return the resulting Future[Assertion] to ScalaTest:
  it should "be remote loaded 3" in {
    Future.sequence(loaders).map { imageElements => {

      // Test 00
      info("All images correct loaded")
      assert(imageElements.forall { img => {
        val pos = Position(img.width, img.height)
        resetCanvasWH(canvas, pos)
        ctx.drawImage(img, 0, 0, img.width, img.height)
        expectedHashCode(getImgName(img.src)) == context2Hashcode(pos)
      }
      })

      /* Composite all pictures drawn outside the play field.
       * This should result in a hashcode equal as the image of the background.
       */
      resetCanvasWH(canvas, initialLUnder)
      val loadedAndNoText0 = new GameState(canvas,
        gameState.pageElements.zip(imageElements).map { case (el, img) => el.copy(img = img) },
        monstersHitTxt = "",
        isNewGame = false)

      // Test 01
      testHarness(loadedAndNoText0,
        "Default initial screen everything left out",
        () => context2Hashcode(initialLUnder) == expectedHashCode("background.png"))

      // ***** Tests with double canvas size
      resetCanvasWH(canvas, doubleInitialLUnder)

      // Test 02
      testHarness(loadedAndNoText0, "Default double size initial screen, no text",
        () => Seq( 1355562831 /*Chrome*/ , 1668792783 /*FireFox*/).contains(context2Hashcode(doubleInitialLUnder)))
      val ref = context2Hashcode(doubleInitialLUnder) // Register the reference value

      val loadedAndSomeText1 = new GameState(canvas,
        gameState.pageElements.zip(imageElements).map { case (el, img) => el.copy(img = img) },
        monstersHitTxt = "Now with text which can differ between browsers",
        isNewGame = false)

      val loadedAndSomeText2 = new GameState(canvas,
        gameState.pageElements.zip(imageElements).map { case (el, img) => el.copy(img = img) },
        monstersHitTxt = "",
        isNewGame = true)

      // Test 03
      testHarness(loadedAndSomeText1, "Test double screen with score text",
        () => ref != context2Hashcode(doubleInitialLUnder))

      // Test 04
      testHarness(loadedAndSomeText2, "Test double screen with explain text put in",
        () => ref != context2Hashcode(doubleInitialLUnder))

      // ***** Test the navigation of the Hero character graphical

      // Test 05
      testHarness(navigateHero(loadedAndNoText0, Position(0, 0)), "Test double screen with centered hero",
        () => Seq(1407150772 /*Chrome*/ , -1212284464 /*FireFox*/, 981419409 ).contains(context2Hashcode(doubleInitialLUnder)))

      // Test 06
      testHarness(navigateHero(loadedAndNoText0, Position(1, 0)), "Test double screen with right displaced hero",
        () => Seq(-1742535935 /*Chrome*/ ,475868743 /*FireFox*/, -1986372876).contains(context2Hashcode(doubleInitialLUnder)))

      // Test 07
      testHarness(navigateHero(loadedAndNoText0, Position(-1, 0)), "Test double screen with left displaced hero",
        () => Seq(2145530953 /*Chrome*/ , 320738379 /*FireFox*/, 214771813).contains(context2Hashcode(doubleInitialLUnder)))

      // Test 08
      testHarness(navigateHero(loadedAndNoText0, Position(0, 1)), "Test double screen with up displaced hero",
        () => Seq(-557901336 /*Chrome*/ ,  -409947707 /*FireFox*/, -1902498081).contains(context2Hashcode(doubleInitialLUnder)))

      // Test 09
      testHarness(navigateHero(loadedAndNoText0, Position(0, -1)), "Test double screen with down displaced hero",
        () => Seq(-1996948634 /*Chrome*/ ,  1484865515 /*FireFox*/, 954791841).contains(context2Hashcode(doubleInitialLUnder)))

      // Test 10  Doesn't work with Google Chrome
      testHarness(loadedAndNoText0, "Test double screen reference still the same.",
        () =>  context2Hashcode(doubleInitialLUnder) == ref)
    }
    }
  }
}
