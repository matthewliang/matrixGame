

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
public class Circle extends Projectile {
	public static final String img_file = "circle.png";
	public static final int SIZE = 40;
	public static final int SPEED = 5;
	private static BufferedImage img;

	public Circle(int courtWidth, int courtHeight, int initGridX, int initGridY) {
		super(initGridX, initGridY, SIZE, SIZE,
				courtWidth, courtHeight, SPEED);
		try {
			if (img == null) {
				img = ImageIO.read(new File(img_file));
			}
		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(img, pos_x, pos_y, width, height, null);
	}
	
	public BufferedImage getImage() {
		return img;
	}
	
	@Override
	public void interact(Neo neo) {
		neo.changeLives(neo.lives - 1);
	}

}
