package hw09;
/**
 * CIS 120 Game HW
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */

import java.awt.*;

/**
 * A basic game object displayed as a yellow circle, starting in the upper left
 * corner of the game court.
 * 
 */
public class Spike extends Projectile {

	public static final int SPEED = 10;
	public int side;

	public Spike(int courtWidth, int courtHeight, int initGridX, int initGridY) {
		super(initGridX, initGridY, 20, 40,
				courtWidth, courtHeight, SPEED);
	}
	
	public void setSide(int s) {
		side = s;
		if (side > 1) {
			super.height = 20;
			super.width = 40;
		}
		setPos(gridX, width, gridY, height);
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.BLUE);
		// 0 = up, 1 = down, 2 = left, 3 = right
		if (side == 0) {
			g.fillPolygon(new int[] {pos_x, pos_x + 10, pos_x + 20},
					new int[] {pos_y, pos_y + 40, pos_y}, 3);
		}
		else if (side == 1) {
			g.fillPolygon(new int[] {pos_x, pos_x + 10, pos_x + 20},
					new int[] {pos_y + 40, pos_y, pos_y + 40}, 3);
		}
		else if (side == 2) {
			g.fillPolygon(new int[] {pos_x, pos_x + 40, pos_x},
					new int[] {pos_y, pos_y + 10, pos_y + 20}, 3);
		}
		else if (side == 3) {
			g.fillPolygon(new int[] {pos_x + 40, pos_x, pos_x + 40},
					new int[] {pos_y, pos_y + 10, pos_y + 20}, 3);
		}
	}
	
	@Override
	public void interact(Neo neo) {
		neo.changeLives(neo.lives - 2);
	}

}
