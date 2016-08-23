package nl.amsscala
package simplegame

import org.scalajs.dom

import scalatags.JsDom.all._

/**
 *
 */
protected trait Page {
  // Create the canvas
  protected val canvas = dom.document.createElement("canvas").asInstanceOf[dom.html.Canvas]
  canvas.setAttribute("crossOrigin", "anonymous")
  private[this] val ctx = canvas.getContext("2d") // .asInstanceOf[dom.CanvasRenderingContext2D]
  private[this] val (bgImage, heroImage, monsterImage) = (Image("img/background.png"), Image("img/hero.png"), Image("img/monster.png"))

  /**
   * Draw everything
   *
   * @param gs Game state to make graphical
   * @return None if not ready else the same GameState if drawn
   */
  protected[simplegame] def render(gs: GameState) = {
    def gameOverTxt = "Game Over?"
    def explainTxt = "Use the arrow keys to\nattack the hidden monster."

    if (bgImage.isReady && heroImage.isReady && monsterImage.isReady) {
      ctx.drawImage(bgImage.element, 0, 0, canvas.width, canvas.height)
      ctx.drawImage(heroImage.element, gs.hero.pos.x, gs.hero.pos.y)
      ctx.drawImage(monsterImage.element, gs.monster.pos.x, gs.monster.pos.y)

      // Score
      ctx.fillStyle = "rgb(250, 250, 250)"
      ctx.font = "24px Helvetica"
      ctx.textAlign = "left"
      ctx.textBaseline = "top"
      ctx.fillText(f"Goblins caught: ${gs.monstersCaught}%03d", 32, 32)

      if (gs.newGame) {
        ctx.textAlign = "center"
        ctx.font = "48px Helvetica"

        ctx.fillText(
          if (gs.isGameOver) gameOverTxt else {
            val txt = explainTxt.split('\n')
            ctx.fillText(txt(1), canvas.width / 2, canvas.height / 2 + 32)
            txt(0)
          }, canvas.width / 2, canvas.height / 2 - 48
        )
      }

      Some(gs)
    } else None
  }

  private class Image(private[this] val src: String, var isReady: Boolean = false) {
    val element = dom.document.createElement("img").asInstanceOf[dom.raw.HTMLImageElement]
    // element.setAttribute("crossOrigin", "anonymous")
    element.onload = (e: dom.Event) => isReady = true
    element.src = src
  }

  private[this] object Image {
    def apply(src: String) = new Image(src)
  }

  canvas.width = dom.window.innerWidth.toInt
  canvas.height = dom.window.innerHeight.toInt - 24
  println(s"Dimension of canvas set to ${canvas.width},${canvas.height}")
  canvas.textContent = "Your browser doesn't support the HTML5 CANVAS tag."

  dom.document.body.appendChild(div(
    cls := "content", style := "text-align:center; background-color:#3F8630;",
    canvas,
    a(href := "http://www.lostdecadegames.com/how-to-make-a-simple-html5-canvas-game/", "Simple HTML5 Canvas game"),
    " ported to ",
    a(href := "http://www.scala-js.org/", "ScalaJS"),
    ". The source code of this game is available on ",
    a(href := "https://github.com/amsterdam-scala/Sjs-Simple-HTML5-canvas-game/", "GitHub"), "."
  ).render)

}
