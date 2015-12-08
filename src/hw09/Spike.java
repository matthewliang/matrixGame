package hw09;
/**
 * CIS 120 Game HW
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * A basic game object displayed as a yellow circle, starting in the upper left
 * corner of the game court.
 * 
 */
public class Spike extends Projectile {
	public static final String img_file = "spike.png";
	public static final int SPEED = 10;
	public int side;
	private static BufferedImage img;

	public Spike(int courtWidth, int courtHeight, int initGridX, int initGridY) {
		super(initGridX, initGridY, 20, 40,
				courtWidth, courtHeight, SPEED);
		try {
			if (img == null) {
				img = ImageIO.read(new File(img_file));
			}
		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}
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
		// 0 = up, 1 = down, 2 = left, 3 = right
		if (side == 0) {
			BufferedImage subImg = img.getSubimage(40, 20, 20, 40);
			g.drawImage(subImg, pos_x, pos_y, width, height, null);
		}
		else if (side == 1) {
			BufferedImage subImg = img.getSubimage(0, 0, 20, 40);
			g.drawImage(subImg, pos_x, pos_y, width, height, null);
		}
		else if (side == 2) {
			BufferedImage subImg = img.getSubimage(0, 40, 40, 20);
			g.drawImage(subImg, pos_x, pos_y, width, height, null);
		}
		else if (side == 3) {
			BufferedImage subImg = img.getSubimage(20, 0, 40, 20);
			g.drawImage(subImg, pos_x, pos_y, width, height, null);
		}
	}
	
	public BufferedImage getImage() {
		return img;
	}
	
	@Override
	public void interact(Neo neo) {
		neo.changeLives(neo.lives - 2);
	}

}
