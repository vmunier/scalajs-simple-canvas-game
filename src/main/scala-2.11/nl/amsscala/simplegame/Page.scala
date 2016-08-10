package nl.amsscala
package simplegame

import org.scalajs.dom

trait Page {

  class Image(src: String, var isReady: Boolean = false) {
    val element = dom.document.createElement("img").asInstanceOf[dom.raw.HTMLImageElement]

    element.onload = (e: dom.Event) => isReady = true
    element.src = src
  }

  object Image {
    def apply(src: String) = new Image(src)
  }

}
