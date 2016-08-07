package sgl
package scene

trait SceneGraphComponent {
  this: GraphicsProvider with InputProvider =>

  /** The main container element to organize a scene
    *
    * Is somewhat similar to a GameScreen interface, but is meant to
    * build a hierarchy of objects, providing chained input processing
    * and rendering into local coordinates.
    *
    * This can automatically organize collection of SceneNode elements,
    * spirtes, etc, with automatically calling their update method, and
    * render them in their own local coordinates.
    *
    * Should be well suited to build GUI such as Game Menus, HUD, and also
    * for simple gameplay screen.
    *
    * Another difference with GameScreen is that this exposes an Event system
    * for the objects in the graph. The goal is really to provide a higher
    * level (likely less efficient) abstraction on top of the core providers
    * of graphics and game features.
    */
  class SceneGraph(width: Int, height: Int) {

    //TODO: width/height are kind of a viewport for the scene graph. They probably should
    //      become some mix of Camera/Viewport
  
    /** Process an input event
      *
      * Each input you wish to handle in the Scene must be processed by the SceneGraph 
      * in order to dispatch it to the right node. The SceneGraph returns true if
      * the event was handled by some node, false if ignored. In most case, the
      * caller will want to ignore an event if it was processed (typically if the
      * caller organizes a HUD on top of its own game map, if the HUD intercepts one
      * input event).
      */
    def processInput(input: Input.InputEvent): Boolean = {
      false
    }
  
    def update(dt: Long): Unit = root.update(dt)
  
    def render(canvas: Canvas): Unit = root.render(canvas)

    /** The root SceneGroup containing all nodes
      *
      * The SceneGraph acts as a sort of SceneGroup, with
      * some additional top level functionalities. It uses
      * internally an instance of a SceneGroup that covers
      * the whole scene to manage the list of nodes added to
      * it.
      */
    val root: SceneGroup = new SceneGroup(0, 0, width, height)

    /** Add a node at the root level of the scene
      *
      * The latest nodes added will overlap the previous ones.
      * So if a node a is added after a node b, and a ends up on top of b,
      * then a will intercept input before b, and also be drawns on top of b
      * (notice how input handling will require the opposite order than rendering)
      */
    def addNode(node: SceneNode): Unit = {
      root.addNode(node)
    }

  }
  
  /** An element that participate to the scene
    *
    * Provides the subdivision of Scene into different parts. A SceneNode
    * could be a simple character sprite, or a button. It could also be a Group
    * of scene element.
    *
    * the position (x,y) is the top-left position of the node, in the coordinate system
    * of the direct parent. So if the node is part of a group, its position can be specified
    * relatively to the group, and then the group can be positioned globally independently.
    * the coordinates use Float, as typically a node could be simulated by physics on a frame-by-frame
    * basis an move fractions of pixels. Double seems to give un-necessary precision.
    *
    * A SceneNode always has a rectangular box around it. The width/height are used as the coordinate
    * system for the scene nodes, and coordinates such as origin points and children nodes are relative
    * to that rectangular area. (0, 0) is, as always, top-left. The node could be a more refined shape,
    * such as a circle inside the box, in that case the rectangle box will need to be a bounding box around,
    * and exact collision method can be defined more precisely against the circle shape.
    */
  abstract class SceneNode(
    var x: Float, var y: Float, var width: Float, var height: Float
  ) {

    //TODO: support for rotation, need to store origin inside the SceneNode coordinate system
    //var originX: Float = 0
    //var originY: Float = 0
    //var scaleX: Float = 1f
    //var scaleY: Float = 1f
    //def scaleBy(scale: Float): Unit = {
    //  scaleX *= scale
    //  scaleY *= scale
    //}
  
  
    def update(dt: Long): Unit

    def render(canvas: Canvas): Unit
  
    //def addAction(action: Action): Unit
  
    /** find and return the SceneNode that is hit by the point (x,y)
      *
      * If the element is a group, it will recursively search for the
      * topmost (visible) element that gets hit. Typically if a button
      * is on top of some panel, and hit is checked with coordinates in
      * the button, then both panel and button are intersected, but the
      * hit method would return the button, as it is displayed on top of
      * the panel.
      */
    def hit(x: Int, y: Int): Option[SceneNode] = {
      if(x >= this.x && x <= this.x + width &&
         y >= this.y && y <= this.y + height)
        Some(this)
      else
        None
    }

  }
  
  /*
   * Should consider the z-order of elements of the group.
   * Latest added elements would be drawn on top.
   */
  class SceneGroup(_x: Float, _y: Float, w: Float, h: Float) extends SceneNode(_x, _y, w, h) {
  
    //nodes are stored in reversed order to when they were added to the scene
    private var nodes: List[SceneNode] = List()
  
    def addNode(node: SceneNode): Unit = {
      nodes ::= node
    }
  
    override def update(dt: Long): Unit = {
      nodes.foreach(_.update(dt))
    }
  
    override def render(canvas: Canvas): Unit = {
      val canvasWidth = canvas.width
      val canvasHeight = canvas.height
      canvas.translate(x.toInt, y.toInt)
      canvas.clipRect(0, 0, width.toInt, height.toInt)

      nodes.reverse.foreach(_.render(canvas))

      canvas.translate(-x.toInt, -y.toInt)
      canvas.clipRect(0, 0, canvasWidth, canvasHeight)
    }
  
    //override def addAction(action: Action): Unit = ???
  
    override def hit(x: Int, y: Int): Option[SceneNode] = {
      var found: Option[SceneNode] = None
      for(node <- nodes if found.isEmpty) {
        found = node.hit(x,y)
      }
      found
    }
  
  }
  object SceneGroup {

    //TODO: shoudl derive x,y,w,h from all the scene nodes
    def apply(els: SceneNode*): SceneGroup = {
      val gr = new SceneGroup(0, 0, 0, 0)
      els.foreach(el => gr.addNode(el))
      gr
    }
  }

}
