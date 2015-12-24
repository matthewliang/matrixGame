

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * A game object displayed as a bomb. Bombs kill Neo instantly.
 * 
 */
public class Bomb extends Projectile {
	public static final String img_file = "bomb.png";
	public static final int SIZE = 40;
	public static final int SPEED = 7;
	private static BufferedImage img;

	public Bomb(int courtWidth, int courtHeight, int initGridX, int initGridY) {
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
	
	@Override
	public void interact(Neo neo) {
		neo.changeLives(neo.lives = 0);
	}
	
	public BufferedImage getImage() {
		return img;
	}
}
