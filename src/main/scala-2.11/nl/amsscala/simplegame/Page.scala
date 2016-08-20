package nl.amsscala
package simplegame

import org.scalajs.dom

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
  canvas.height = dom.window.innerHeight.toInt - 32
  println(s"Dimension of canvas set to ${canvas.width},${canvas}")
  canvas.textContent = "Your browser doesn't support the HTML5 CANVAS tag."

  dom.document.body.appendChild(canvas)

}
