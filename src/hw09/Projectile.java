package hw09;
/**
 * CIS 120 Game HW
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */

import java.awt.Graphics;

/** An object in the game. 
 *
 *  Game objects exist in the game court. They have a position, 
 *  velocity, size and bounds. Their velocity controls how they 
 *  move; their position should always be within their bounds.
 */
public class Projectile {

	/** Current position of the object (in terms of graphics coordinates)
	 *  
	 * Coordinates are given by the upper-left hand corner of the object.
	 * This position should always be within bounds.
	 *  0 <= pos_x <= max_x 
	 *  0 <= pos_y <= max_y 
	 */
	public int pos_x; 
	public int pos_y;
	public int gridX;
	public int gridY;

	/** Size of object, in pixels */
	public int width;
	public int height;
	
	/** Velocity: number of pixels to move every time move() is called */
	public int v_x = 0;
	public int v_y = 0;
	public int speed = 8;

	/**
	 * Constructor
	 */
	public Projectile(int gridX, int gridY, 
		int width, int height, int court_width, int court_height, int speed){
		if (gridX == 0) {
			this.v_x = speed;
		}
		else if (gridX == 7) {
			this.v_x = -speed;
		}
		if (gridY == 0) {
			this.v_y = speed;
		}
		else if (gridY == 7) {
			this.v_y = -speed;
		}
		setPos(gridX, width, gridY, height);
		this.gridX = gridX;
		this.gridY = gridY;
		this.width = width;
		this.height = height;
		this.speed = speed;
	}

	public void setPos(int gridX, int width, int gridY, int height) {
		this.pos_x = 50 * gridX + (50 - width) / 2;
		this.pos_y = 50 * gridY + (50 - height) / 2;
	}
	
	/**
	 * Moves the object by its velocity.  Ensures that the object does
	 * not go outside its bounds by clipping.
	 */
	public void move(){
		pos_x += v_x;
		pos_y += v_y;
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
