package sgl.html5
package themes

import org.scalajs.dom
import dom.html

/** Specify how the canvas app interact with the web page
  *
  * This is some re-usable bit of code, that can be configured
  * when setting up the HTML5 app. It's only relevant to html5
  * based games, so the interface is not visible to the rest
  * of the SGL.
  *
  * The general idea of the SGL is to not be concerned with things such
  * as windowing or containers, and only expose a canvas to be drawn on.
  * This means that deployment questions such as frame dimension, fullscreen
  * or not, are left to the different backend wiring. The idea of providing
  * a theme is to help package standard website for html5 games.
  *
  * We define the theme as an external object, as we didn't wanted
  * to mix the decision of where to display the canvas, with the actual
  * SGL code that handle the content of the canvas. Having an external theme,
  * with a NoTheme option as well, let the user decides exactly how he wishes
  * to inject a canvas game into his website.
  */
abstract class Theme {

  def init(canvas: html.Canvas): Unit

}

class NoTheme extends Theme {

  override def init(canvas: html.Canvas): Unit = {}

}
