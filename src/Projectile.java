

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

/** An object in the game. 
 *
 *  Game objects exist in the game court. They have a position, 
 *  velocity, size and bounds. Their velocity controls how they 
 *  move; their position should always be within their bounds.
 */
public abstract class Projectile {

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
	public boolean intersects(Neo neo){
		return (pos_x + width >= neo.pos_x
				&& pos_y + height >= neo.pos_y
				&& neo.pos_x + neo.width >= pos_x 
				&& neo.pos_y + neo.height >= pos_y);
	}
	
	public boolean hasCollision(Neo neo) {
        if (this.intersects(neo)) {
        	Rectangle thisBounds = new Rectangle();
        	thisBounds.setSize(width, height);
        	thisBounds.setLocation(pos_x, pos_y);
        	Rectangle neoBounds = new Rectangle();
        	neoBounds.setSize(neo.width, neo.height);
        	neoBounds.setLocation(neo.pos_x, neo.pos_y);
        	Area thisArea = new Area(thisBounds);
        	Area neoArea = new Area(neoBounds);
        	thisArea.intersect(neoArea);
            Rectangle bounds = thisArea.getBounds();
            if (!bounds.isEmpty()) {
                for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
                    for (int y = bounds.y; y < bounds.y + bounds.height; y++) {
                        if (collision(x, y, neo)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
	public boolean collision(int x, int y, Neo neo) {
        boolean collision = false;
        BufferedImage neoImg = Neo.getImage();
        BufferedImage projectileImg = getImage();
        int projectilePixel = projectileImg.getRGB(x - pos_x, y - pos_y);
        int neoPixel = neoImg.getRGB(x - neo.getXPos(), y - neo.getYPos());
        if (((projectilePixel >> 24) & 0xFF) < 255 && ((neoPixel >> 24) & 0xFF) < 255) {
            collision = true;
        }
        return collision;
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
	public abstract void draw(Graphics g);
	
	public abstract void interact(Neo neo);
	
	public abstract BufferedImage getImage();
}
