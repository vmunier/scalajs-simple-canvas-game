package nl.amsscala
package simplegame

import org.scalajs.dom
import org.scalajs.dom.ext.KeyCode.{Down, Left, Right, Up}

import scala.collection.mutable

// TODO: http://stackoverflow.com/questions/12370244/case-class-copy-method-with-superclass

/**
 * Umbrella for `Page`, `Hero` and `Monster` Abstract Data Types.
 * @tparam Numeric Numeric generic abstraction
 */
sealed trait CanvasComponent[Numeric] {
  val pos: Position[Numeric]
  val img: dom.raw.HTMLImageElement

  def copy(img: dom.raw.HTMLImageElement): CanvasComponent[Numeric]

  def src: String

  override def toString = s"${this.getClass.getSimpleName} $pos"

  override def equals(that: Any): Boolean = that match {
    case that: CanvasComponent[Numeric] => this.pos == that.pos
    case _ => false
  }

}

/**
 * `CanvasComponent`'s implementation for to visual back ground on the canvas.
 *
 * @param pos Playground position, defaulted to (0,0)
 * @param img HTML image
 * @tparam G  Numeric generic abstraction
 */
final class Playground[G](val pos: Position[G] = Position(0, 0).asInstanceOf[Position[G]],
                    val img: dom.raw.HTMLImageElement = null) extends CanvasComponent[G] {

  def copy(img: dom.raw.HTMLImageElement): Playground[G] = new Playground(img = img)

  def src = "img/background.png"
}

/**
 * `CanvasComponent`'s implementation for to visual Monster sprite on the canvas.
 * @param pos Monsters' position
 * @param img HTML image
 * @tparam M  Numeric generic abstraction
 */
final class Monster[M](val pos: Position[M], val img: dom.raw.HTMLImageElement) extends CanvasComponent[M] {
  /** Set a Monster at a (new) random position */
  def copy[D: Numeric](canvas: dom.html.Canvas) = new Monster(Monster.randomPosition[D](canvas), img)
  /** Load the img in the Element */
  def copy(image: dom.raw.HTMLImageElement) = new Monster(pos, image)

  def src = "img/monster.png"

}

/**
 * Companion object of class `Monster`
 */
object Monster {
  def apply[M: Numeric](canvas: dom.html.Canvas, randPos: Position[M]) = new Monster(randPos, null)

  private[simplegame] def randomPosition[M: Numeric](canvas: dom.html.Canvas): Position[M] = {
    @inline def compute(dim: Int) = (math.random * (dim - Hero.pxSize)).toInt
    Position(compute(canvas.width), compute(canvas.height)).asInstanceOf[Position[M]]
  }
}

/**
 * `CanvasComponent`'s implementation for to visual Hero sprite on the canvas.
 * @param pos Heros' position
 * @param img HTML image
 * @tparam H  Numeric generic abstraction
 */
final class Hero[H: Numeric](val pos: Position[H], val img: dom.raw.HTMLImageElement) extends CanvasComponent[H] {

  def copy(img: dom.raw.HTMLImageElement) = new Hero(pos, img)

  def copy(canvas: dom.html.Canvas) = new Hero(SimpleCanvasGame.center(canvas).asInstanceOf[Position[H]], img)

  def copy(pos: Position[H]) = new Hero(pos, img)

  /**
   * Check if the square area is within the rectangle area of the `<canvas>`
   *
   * @param canvas Canvas where to
   * @return False  if a square out of bound
   */
  protected[simplegame] def isValidPosition(canvas: dom.html.Canvas) =
    pos.isValidPositionEl(SimpleCanvasGame.canvasDim[H](canvas), Hero.pxSize.asInstanceOf[H])

  def src = "img/hero.png"

  /**
   * Compute new position of hero according to the keys pressed
   * @param latency  Time since previous update.
   * @param keysDown Set of the keys pressed.
   * @return         Computed move Hero
   */
  protected[simplegame] def keyEffect(latency: Double, keysDown: mutable.Set[Int]): Hero[H] = {

    // Convert pressed keyboard keys to coordinates
    @inline
    def displacements: mutable.Set[Position[H]] = {
      def dirLookUp = Map(// Key to direction translation
        Left -> Position(-1, 0), Right -> Position(1, 0), Up -> Position(0, -1), Down -> Position(0, 1)
      ).withDefaultValue(Position(0, 0))

      keysDown.map { k => dirLookUp(k).asInstanceOf[Position[H]] }
    }

    // Compute next position by summing all vectors with the position where the hero is found.
    copy(displacements.fold(pos) { (z, vec) => z + vec * (Hero.speed * latency).toInt.asInstanceOf[H] })
  }

}

/** Companion object of class `Hero`. */
object Hero {
  protected[simplegame] val (pxSize, speed) = (32, 256)

  /** Hero image centered in the field */
  def apply[H: Numeric](canvas: dom.html.Canvas): Hero[H] =
   Hero[H](SimpleCanvasGame.center(canvas).asInstanceOf[Position[H]])

  def apply[H: Numeric](pos : Position[H]) =new Hero(pos, null)
}
