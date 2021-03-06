

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

public class Neo extends GameObj {
	public static final String img_file = "neo.png";
	public static final String img_hit_file = "neoRed.png";
	private static BufferedImage img;
	private static BufferedImage imgHit;
	public static final int HEIGHT = 50;
	public static final int WIDTH = 30;
	public static final int INIT_GRID_X = 4;
	public static final int INIT_GRID_Y = 3;
	public int lives = 3;
	public boolean invulnerable = false;

	public Neo(int courtWidth, int courtHeight) {
		super(INIT_GRID_X, INIT_GRID_Y, WIDTH, HEIGHT, courtWidth,
				courtHeight);
		this.min_x = 50;
		this.min_y = 50;
		this.max_x = 310;
		this.max_y = 310;
		
		try {
			if (img == null) {
				img = ImageIO.read(new File(img_file));
			}
			if (imgHit == null) {
				imgHit = ImageIO.read(new File(img_hit_file));
			}
		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}
	}

	public void move(String s) {
		switch (s) {
		case "left": {
			if (isValid(pos_x - 50, pos_y)) {
				this.pos_x = this.pos_x - 50;
			}
			break;
		}
		case "right": {
			if (isValid(pos_x + 50, pos_y)) {
				pos_x += 50;
			}
			break;
		}
		case "up": {
			if (isValid(pos_x, pos_y - 50)) {
				pos_y -= 50;
			}
			break;
		}
		case "down": {
			if (isValid(pos_x, pos_y + 50)) {
				pos_y += 50;
			}
			break;
		}
		}
	}
	
	public boolean isValid(int x, int y) {
		return !(x < min_x || y < min_y || x > max_x || y > max_y);
	}
	
	public void changeLives(int numLives) {
		lives = numLives;
	}
	
	public static BufferedImage getImage() {
		return img;
	}
	
	public int getXPos() {
		return this.pos_x;
	}
	
	public int getYPos() {
		return this.pos_y;
	}
	
	@Override
	public void draw(Graphics g) {
		if (!invulnerable) {
			g.drawImage(img, pos_x, pos_y, width, height, null);
		}
		else {
			g.drawImage(imgHit, pos_x, pos_y, width, height, null);
		}
	}

}
