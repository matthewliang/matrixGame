

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Heart extends GameObj{
	public static final String img_file = "heart.png";
	public static final int SIZE = 40;
	private static BufferedImage img;
	
	public Heart(int courtWidth, int courtHeight, int initGridX, int initGridY) {
	super(initGridX, initGridY, SIZE, SIZE, courtWidth,
			courtHeight);
	
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
	
	public void interact(Neo neo) {
		if (neo.lives < 5) {
		neo.changeLives(neo.lives + 1);
		}
		else {
			GameCourt.changeScore(5);
		}
	}
}
