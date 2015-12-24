

import java.awt.Graphics;

/** An object in the game. 
 *
 *  Game objects exist in the game court. They have a position, 
 *  size and bounds. Their velocity controls how they 
 *  move.
 */
public class GameObj {

	/** Current position of the object (in terms of graphics coordinates)
	 *  
	 * Coordinates are given by the upper-left hand corner of the object.
	 */
	public int pos_x; 
	public int pos_y;

	/** Size of object, in pixels */
	public int width;
	public int height;

	/** Upper bounds of the area in which the object can be positioned.  
	 *    Maximum permissible x, y positions for the upper-left 
	 *    hand corner of the object
	 */
	public int max_x;
	public int max_y;
	public int min_x;
	public int min_y;

	/**
	 * Constructor
	 */
	public GameObj(int gridX, int gridY, 
		int width, int height, int court_width, int court_height){
		this.pos_x = 50 * gridX + (50 - width) / 2;
		this.pos_y = 50 * gridY + (50 - height) / 2;
		this.width = width;
		this.height = height;
		
		// take the width and height into account when setting the 
		// bounds for the upper left corner of the object.
		this.max_x = court_width - width;
		this.max_y = court_height - height;

	}

	/**
	 * Determine whether this game object is currently intersecting
	 * another object.
	 * 
	 * Intersection is determined by comparing bounding boxes. If the 
	 * bounding boxes overlap, then an intersection is considered to occur.
	 * 
	 * @param obj : other object
	 * @return whether this object intersects the other object.
	 */
	public boolean intersects(GameObj obj){
		return (pos_x + width >= obj.pos_x
				&& pos_y + height >= obj.pos_y
				&& obj.pos_x + obj.width >= pos_x 
				&& obj.pos_y + obj.height >= pos_y);
	}
	
	/**
	 * Default draw method that provides how the object should be drawn 
	 * in the GUI. This method does not draw anything. Subclass should 
	 * override this method based on how their object should appear.
	 * 
	 * @param g 
	 *	The <code>Graphics</code> context used for drawing the object.
	 * 	Remember graphics contexts that we used in OCaml, it gives the 
	 *  context in which the object should be drawn (a canvas, a frame, 
	 *  etc.)
	 */
	public void draw(Graphics g) {
	}
	
	public void interact(Neo neo) {
	}
}
