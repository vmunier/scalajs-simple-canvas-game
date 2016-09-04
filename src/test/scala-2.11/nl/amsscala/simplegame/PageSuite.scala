package nl.amsscala.simplegame

import org.scalajs.dom

import scala.scalajs.js
import scalatags.JsDom.all._

class PageSuite extends SuiteSpec {
  val page = new Page {
    canvas.width = 1242 // 1366
    canvas.height = 674 // 768
  }

  describe("A Hero") {
    describe("should tested within the limits") {

      it("good path") {
        {
          page.render(GameState(Hero(621, 337), Monster(0, 0), 0, false))
          val imageData: scala.collection.mutable.Seq[Int] =
            page.ctx.getImageData(0, 0, page.canvas.width, page.canvas.height).data

          imageData.hashCode() shouldBe -1753260013
        }

        {
          page.render(GameState(Hero(365, 81), Monster(0, 0), 0, false))

          dom.document.body.appendChild(div(
            cls := "content", style := "text-align:center; background-color:#3F8630;",
            canvas
          ).render)

          val imageData = page.ctx.getImageData(0, 0, page.canvas.width, page.canvas.height)

          // imageData.data.sum shouldBe -1753260013
        }

        {
          page.render(GameState(Hero(877, 593), Monster(0, 0), 0, false))
          val imageData: scala.collection.mutable.Seq[Int] =
            page.ctx.getImageData(0, 0, page.canvas.width, page.canvas.height).data

          imageData.hashCode() shouldBe -1753260013
        }

      }

    }
  }
}
