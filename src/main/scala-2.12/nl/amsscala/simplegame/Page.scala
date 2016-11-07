package nl.amsscala
package simplegame

import org.scalajs.dom

import scala.concurrent.{Future, Promise}
import scalatags.JsDom.all._

/** Everything related to Html5 visuals as put on a HTML page. */
trait Page { //Create canvas with a 2D processor
  val canvas = dom.document.createElement("canvas").asInstanceOf[dom.html.Canvas]
  private [simplegame] val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

  private lazy val postponed =   // Create the HTML body element with content
    dom.document.body.appendChild(div(cls := "content", style := "text-align:center; background-color:#3F8630;", canvas,
    a(href := "http://www.lostdecadegames.com/how-to-make-a-simple-html5-canvas-game/",
      title := s"This object code is compiled with type parameter ${genericDetect(0D.asInstanceOf[SimpleCanvasGame.T])}.",
      "Simple HTML5 Canvas game"), " ported to ",
    a(href := "http://www.scala-js.org/", "Scala.js")).render)

  /**
   * Draw everything accordingly the given `GameState`.
   *
   * Order: Playground, Monster, Hero, monstersHitTxt, explainTxt/gameOverTxt
   *
   * @param gs Game state to make the graphics.
   * @return The same gs
   */
  def render[T](gs: GameState[T]) = {
    postponed
    // Draw each page element in the specific list order
    gs.pageElements.foreach(pe => {
      def drawImage(resize: Position[Int]) =
        ctx.drawImage(pe.img, pe.pos.x.asInstanceOf[Int], pe.pos.y.asInstanceOf[Int], resize.x, resize.y)

      drawImage(pe match {
        case _: Playground[_] => canvasDim[Int](canvas)
        case pm: CanvasComponent[_] => dimension(pm.img) // The otherwise or default clause
      })
    })

    ctx.fillStyle = "rgb(250, 250, 250)"
    // Score
    if (!gs.monstersHitTxt.isEmpty) {
      ctx.font = "24px Helvetica"
      ctx.textAlign = "left"
      ctx.textBaseline = "top"
      ctx.fillText(gs.monstersHitTxt, 32, 32)
    }

    if (gs.isNewGame) {
      val centr = center(canvas)

      ctx.textAlign = "center"
      ctx.font = "48px Helvetica"

      ctx.fillText(
        if (gs.isGameOver) gs.gameOverTxt
        else {
          val txt = gs.explainTxt.split('\n')
          ctx.fillText(txt.last, centr.x, centr.y + 32)
          txt.head
        }, centr.x, centr.y - 48
      )
    }
    gs
  }

  def center(cnvs: dom.html.Canvas) = Position(canvas.width / 2, canvas.height / 2)

  def canvasDim[D](cnvs: dom.html.Canvas) = Position(cnvs.width, cnvs.height).asInstanceOf[Position[D]]

  canvas.textContent = "Your browser doesn't support the HTML5 CANVAS tag."
  resetCanvasWH(canvas, Position(dom.window.innerWidth, dom.window.innerHeight - 25))

  @inline private def dimension(img: dom.raw.HTMLImageElement) = Position(img.width, img.height)

  /** Convert the onload event of an img tag into a Future */
  def imageFuture(src: String): Future[dom.raw.HTMLImageElement] = {
    val img = dom.document.createElement("img").asInstanceOf[dom.raw.HTMLImageElement]

    // Tackling CORS enabled images
    img.setAttribute("crossOrigin", "anonymous")
    img.src = src
    if (img.complete) Future.successful(img)
    else {
      val p = Promise[dom.raw.HTMLImageElement]()
      img.onload = { (e: dom.Event) => p.success(img) }
      p.future
    }
  }

  /**
   * Set canvas dimension
   * @param cnvs Canvas element
   * @param pos  Dimension in `Position[P]`
   * @tparam P   Numeric generic type
   */
  @inline
  def resetCanvasWH[P: Numeric](cnvs: dom.html.Canvas, pos: Position[P]) = {
    cnvs.width = pos.x.asInstanceOf[Int]
    cnvs.height = pos.y.asInstanceOf[Int]
  }

  private def genericDetect(x: Any) = x match {
    case _: Long => "Long"
    case _: Int => "Int"
    case _: Double => "Double"
    case _ => "unknown"
  }
}
