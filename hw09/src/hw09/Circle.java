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
public class Circle extends Projectile {

	public static final int SIZE = 40;
	public static final int SPEED = 5;

	public Circle(int courtWidth, int courtHeight, int initGridX, int initGridY) {
		super(initGridX, initGridY, SIZE, SIZE,
				courtWidth, courtHeight, SPEED);
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.GREEN);
		g.fillOval(pos_x, pos_y, width, height);
	}
	
	@Override
	public void interact(Neo neo) {
		neo.changeLives(neo.lives - 1);
	}

}
