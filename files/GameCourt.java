

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * GameCourt
 * 
 * This class holds the primary game logic for how different objects interact
 * with one another. Take time to understand how the timer interacts with the
 * different methods and how it repaints the GUI on every tick().
 * 
 */
@SuppressWarnings("serial")
public class GameCourt extends JPanel {

	// the state of the game logic
	private Neo neo; // character that the player moves

	public State state; // which screen the user is on

	// Game constants
	public static final int COURT_WIDTH = 400;
	public static final int COURT_HEIGHT = 430;
	// Update interval for timer, in milliseconds
	public static final int INTERVAL = 35;
	public static final int GRACE_TIME = 15;
	// Probabilities of spikes and bombs spawning
	public static final int INIT_SPIKE_PROB = 1;
	public static final int INIT_BOMB_PROB = 0;
	
	public static int score = 0;
	
	public int counter = 0;
	public int graceTimer = 0;
	
	public static int spikeProb = INIT_SPIKE_PROB;
	public static int bombProb = INIT_BOMB_PROB;
	
	public static final String menuImg_file = "matrixMenu.png";
	private static BufferedImage menuImg;
	
	public static final String img_file = "heartSmall.png";
	private static BufferedImage img;
	
	public static final String highscores_file = "highscores.txt";
	
	public static Random rand = new Random();
	
	public static ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	public static ArrayList<GameObj> bonuses = new ArrayList<GameObj>();

	public GameCourt() {
		try {
			if (img == null) {
				img = ImageIO.read(new File(img_file));
			}
			if (menuImg == null) {
				menuImg = ImageIO.read(new File(menuImg_file));
			}
		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}
		
		// creates border around the court area, JComponent method
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		// The timer is an object which triggers an action periodically
		// with the given INTERVAL. One registers an ActionListener with
		// this timer, whose actionPerformed() method will be called
		// each time the timer triggers. We define a helper method
		// called tick() that actually does everything that should
		// be done in a single timestep.
		Timer timer = new Timer(INTERVAL, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tick();
			}
		});
		timer.start(); // MAKE SURE TO START THE TIMER!

		// Enable keyboard focus on the court area.
		// When this component has the keyboard focus, key
		// events will be handled by its key listener.
		setFocusable(true);

		// This key listener allows the square to move as long
		// as an arrow key is pressed, by changing the square's
		// velocity accordingly. (The tick method below actually
		// moves the square.)
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					neo.move("left");
				}
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
					neo.move("right");
				else if (e.getKeyCode() == KeyEvent.VK_DOWN)
					neo.move("down");
				else if (e.getKeyCode() == KeyEvent.VK_UP)
					neo.move("up");
			}

			public void keyReleased(KeyEvent e) {
			}
		});

		// Detects user's clicks of various buttons
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (state == State.MENU) {
					if (e.getX() > 165 && e.getX() < 235 &&
							e.getY() > 205 && e.getY() < 230) {
						reset();
					}
					else if (e.getX() > 90 && e.getX() < 305 &&
							e.getY() > 240 && e.getY() < 265) {
						showInstructions();
					}
					else if (e.getX() > 100 && e.getX() < 300 &&
							e.getY() > 280 && e.getY() < 305) {
						showHighScores();
					}
				}
				else if (state == State.INSTRUCTIONSCREEN || state == State.HIGHSCORESCREEN) {
					if (e.getX() > 170 && e.getX() < 240 &&
							e.getY() > 10 && e.getY() < 35) {
						showMenu();
					}
				}
				else if (state == State.GAMEOVER) {
					if (e.getX() > 65 && e.getX() < 335 &&
							e.getY() > 310 && e.getY() < 345) {
						reset();
					}
				}
			}
		});
	}

	/**
	 * (Re-)set the game to its initial state.
	 */
	public void reset() {

		neo = new Neo(COURT_WIDTH, COURT_HEIGHT);
		projectiles = new ArrayList<Projectile>();
		bonuses = new ArrayList<GameObj>();

		state = State.PLAYING;
		score = 0;
		spikeProb = INIT_SPIKE_PROB;
		bombProb = INIT_BOMB_PROB;

		// Make sure that this component has the keyboard focus
		requestFocusInWindow();
	}

	/**
	 * This method is called every time the timer defined in the constructor
	 * triggers.
	 */
	void tick() {
		if (state == State.PLAYING) {
			
			int heartSpawn = rand.nextInt(100);
			// adds a heart with probability 1/100
			if (heartSpawn < 1) {
				addHeart();
			}
			// checks if player collects a heart
			java.util.Iterator<GameObj> bonusIter = bonuses.iterator();
			while (bonusIter.hasNext()) {
				GameObj b = bonusIter.next();
				if (b.intersects(neo)) {
					b.interact(neo);
					bonusIter.remove();
				}
			}
			
			// releases a new projectile every 10 frames
			if (counter % 10 == 0) {
				score++;
				addProjectile();
			}
			
			// checks if there are collisions with any projectiles
			java.util.Iterator<Projectile> projIter = projectiles.iterator();
			while (projIter.hasNext()) {
				Projectile p = projIter.next();
				p.move();
				if (!neo.invulnerable) {
					if (p.hasCollision(neo)) {
						p.interact(neo);
						if (neo.lives <= 0) {
							state = State.GAMEOVER;
						}
						neo.invulnerable = true;
						if (state == State.PLAYING) {
							projIter.remove();
						}
					}
				}

				// deletes projectiles going off the screen
				if (p.pos_x < 0 || p.pos_y < 0 || p.pos_x > 400 || p.pos_y > 400) {
					projIter.remove();
				}
			}
			
			counter++;
			
			// gives the player a fixed time before they can be hit again
			if (neo.invulnerable) {
				graceTimer++;
				if (graceTimer == GRACE_TIME) {
					neo.invulnerable = false;
					graceTimer = 0;
				}
			}
			
			// increases the difficulty every 500 frames
			if (counter % 500 == 0) {
				spikeProb ++;
				bombProb ++;
			}
			
			// update the display
			repaint();
			
			// displays game over or allows entry of high scores
			if (state == State.GAMEOVER) {
				try {
					ArrayList<Integer> hsList = getHighScores();
					if (hsList == null || score >= Collections.min(hsList) || hsList.size() < 10) {
						String playerName = getPlayerName("");
						if (playerName != null) {
							addHighScore(playerName, score);
							showHighScores();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Changes the user's score
	 * @param x Adds x to score
	 */
	public static void changeScore(int x) {
		score += x;
	}

	/**
	 * Spawns a heart randomly
	 */
	public static void addHeart() {
		int heartX = rand.nextInt(6) + 1;
		int heartY = rand.nextInt(6) + 1;
		Heart h = new Heart(COURT_WIDTH, COURT_HEIGHT, heartX, heartY);
		boolean exists = false;
		for (GameObj b : bonuses) {
			if (b.pos_x == h.pos_x && b.pos_y == h.pos_y) {
				exists = true;
			}
		}
		if (!exists) {
			bonuses.add(h);
		}
	}
	
	/**
	 * Spawns a projectile randomly
	 */
	public static void addProjectile() {
		int side = rand.nextInt(4); // 0 = up, 1 = down, 2 = left, 3 = right
		int position = rand.nextInt(6) + 1;
		int initGridX = 0;
		int initGridY = 0;
		switch (side) {
		case 0: {
			initGridX = position;
			initGridY = 0;
			break;
		}
		case 1: {
			initGridX = position;
			initGridY = 7;
			break;
		}
		case 2: {
			initGridX = 0;
			initGridY = position;
			break;
		}
		case 3: {
			initGridX = 7;
			initGridY = position;
			break;
		}
		}
		
		int type = rand.nextInt(10); // 0 - 6 = circle, 7 - 8 = spike, 9 = bomb
		if (type < bombProb) {
			Bomb b = new Bomb(COURT_WIDTH, COURT_HEIGHT, initGridX, initGridY);
			projectiles.add(b);
		}
		else if (type < bombProb + spikeProb) {
			Spike s = new Spike(COURT_WIDTH, COURT_HEIGHT, initGridX, initGridY);
			s.setSide(side);
			projectiles.add(s);
		}
		else {
			Circle c = new Circle(COURT_WIDTH, COURT_HEIGHT, initGridX, initGridY);
			projectiles.add(c);
		}
	}
	
	/**
	 * Returns the 10 highest scores
	 * @return ArrayList of 10 highest scores
	 * @throws IOException
	 */
	public ArrayList<Integer> getHighScores() throws IOException {
		ArrayList<Integer> highScores = new ArrayList<Integer>();
		try {
			Reader r = new FileReader(highscores_file);
			BufferedReader br = new BufferedReader(r);
			String line = br.readLine();
			
			while (line != null && highScores.size() < 10) {
				int highScore = Integer.parseInt(line.substring(line.indexOf(" ") + 1));
				highScores.add(highScore);
				line = br.readLine();
			}
			br.close();
			r.close();
			return highScores;
		} catch (FileNotFoundException e) {
			return null;
		}
	}
	
	/**
	 * Allows entry of user's name. Checks for invalid input
	 * @param additionalMessage
	 * @return Player's names
	 */
	public String getPlayerName(String additionalMessage)
	{
		String highScoreMessage = additionalMessage + "You scored " + score +
				"!\n Enter name to save high score:";
		JFrame highScoreFrame = new JFrame();
	    String playerName = JOptionPane.showInputDialog(highScoreFrame,
	    		highScoreMessage,
	    		"Game Over", JOptionPane.INFORMATION_MESSAGE);

	    if (playerName == null) {
	    	return null;
	    }
	    
	    if(playerName.isEmpty() || !playerName.matches("[A-Za-z]*") || playerName.length() > 10)
	    {
	        playerName = getPlayerName
	        		("Please enter a name under 10 letters long.\n");
	    }

	    return playerName;
	}
	
	/**
	 * Adds a high score to the high score list
	 * @param name
	 * @param score
	 * @throws IOException
	 */
	public void addHighScore(String name, int score) throws IOException {
		try {
			Reader r = new FileReader(highscores_file);
			ArrayList<String> lines = new ArrayList<String>();
			BufferedReader br = new BufferedReader(r);
			String line = br.readLine();
			boolean added = false;
			
			while (line != null) {
				int highScore = Integer.parseInt(line.substring(line.indexOf(" ") + 1));
				if (score >= highScore && !added) {
					lines.add(name + " " + score);
					added = true;
				}
				lines.add(line);
				line = br.readLine();
			}
			if (!added) {
				lines.add(name + " " + score);
			}
			br.close();
			r.close();

			File hsFile = new File(highscores_file);
	        BufferedWriter writer = new BufferedWriter(new FileWriter(hsFile));
            for(String s : lines) {
                 writer.write(s + "\n");
            }
            writer.flush();
            writer.close();
		} catch (FileNotFoundException e) {
	        File hsFile = new File(highscores_file);
	        BufferedWriter writer = new BufferedWriter(new FileWriter(hsFile));
	        writer.write(name + " " + score);
	        writer.close();
		} catch (IOException e) {
			throw new IOException();
		}
	}
	
	public void showMenu() {
		state = State.MENU;
		repaint();
	}
	
	public void showInstructions() {
		state = State.INSTRUCTIONSCREEN;
		repaint();
	}
	
	public void showHighScores() {
		state = State.HIGHSCORESCREEN;
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (state == State.MENU) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, COURT_WIDTH, COURT_HEIGHT);
			g.drawImage(menuImg, 0, 0, 400, 400, null);
		}
		if (state == State.PLAYING || state == State.GAMEOVER) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, COURT_WIDTH, COURT_HEIGHT);
		g.setColor(Color.BLACK);
		g.drawRect(50, 50, 300, 300);
		g.drawLine(0, 400, 400, 400);
		neo.draw(g);
		for (Projectile p : projectiles) {
			p.draw(g);
		}
		for (GameObj b : bonuses) {
			b.draw(g);
		}
		
		int heartWidth = neo.lives * 11 - 1;
		if (heartWidth > 0) {
			BufferedImage subImg = img.getSubimage(0, 0, heartWidth, 10);
			int heartXPos = 200 - neo.lives * 5;
			g.drawImage(subImg, heartXPos, 405, heartWidth, 10, null);
		}
		}
		Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	            RenderingHints.VALUE_ANTIALIAS_ON);
        Font font = new Font("Arial", Font.BOLD, 11);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds("Score: " + score, g2);
        int x = (this.getWidth() - (int) rect.getWidth()) / 2;
        g.drawString("Score: " + score, x, 425);
		
		if (state == State.GAMEOVER) {
			drawGameOver(g);
		}
		else if (state == State.HIGHSCORESCREEN) {
			drawHighScoreScreen(g, score);
		}
		else if (state == State.INSTRUCTIONSCREEN) {
			drawInstructionScreen(g);
		}
	}
	
	public void drawGameOver(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	            RenderingHints.VALUE_ANTIALIAS_ON);
        Font font = new Font("Trajan Pro", Font.PLAIN, 45);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds("Game Over!", g2);
        int x = (this.getWidth() - (int) rect.getWidth()) / 2;
		g.clearRect(51, 51, 299, 50);
        g.drawString("Game Over!", x, 100);
        
        font = new Font("Trajan Pro", Font.PLAIN, 45);
        g2.setFont(font);
        fm = g2.getFontMetrics();
        rect = fm.getStringBounds("Play Again", g2);
        x = (this.getWidth() - (int) rect.getWidth()) / 2;
        g.clearRect(51, 300, 299, 50);
        g.drawString("Play Again", x, 345);
	}

	public void drawHighScoreScreen(Graphics g, int score) {
		g.clearRect(0, 0, COURT_WIDTH, COURT_HEIGHT);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	            RenderingHints.VALUE_ANTIALIAS_ON);
        Font font = new Font("Trajan Pro", Font.PLAIN, 48);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds("High Scores", g2);
        int x = (this.getWidth() - (int) rect.getWidth()) / 2;
        g.drawString("High Scores", x, 100);
        
        font = new Font("Trajan Pro", Font.PLAIN, 20);
        g2.setFont(font);
        fm = g2.getFontMetrics();
        rect = fm.getStringBounds("Back", g2);
        x = (this.getWidth() - (int) rect.getWidth()) / 2;
        g.drawString("Home", x, 30);
        
        ArrayList<String> highScoreNames = new ArrayList<String>();
        ArrayList<String> highScores = new ArrayList<String>();
		try {
			Reader r = new FileReader(highscores_file);
			BufferedReader br = new BufferedReader(r);
			String line = br.readLine();
			
			while (line != null && highScores.size() < 10) {
				int spaceIndex = line.indexOf(" ");
				highScoreNames.add(line.substring(0, spaceIndex));
				highScores.add(line.substring(spaceIndex + 1));
				line = br.readLine();
			}
			br.close();
			r.close();
		} catch (IOException e) {
			// do nothing
		}
		
		font = new Font("Arial", Font.PLAIN, 20);
		g2.setFont(font);
		int newScoreIndex = highScores.indexOf(Integer.toString(score));
		for (int i = 0; i < highScores.size(); i++) {
			int yPos = 140 + 25 * i;
			g2.setColor(Color.BLACK);
			if (i == newScoreIndex) {
				g2.setColor(Color.RED);
			}
			g.drawString((i + 1) + ". ", 65, yPos);
			g.drawString(highScoreNames.get(i), 95, yPos);
			g.drawString(highScores.get(i), 265, yPos);
		}
	}
	
	public void drawInstructionScreen(Graphics g) {
		g.clearRect(0, 0, COURT_WIDTH, COURT_HEIGHT);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	            RenderingHints.VALUE_ANTIALIAS_ON);
        Font font = new Font("Trajan Pro", Font.PLAIN, 48);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds("Instructions", g2);
        int x = (this.getWidth() - (int) rect.getWidth()) / 2;
        g.drawString("Instructions", x, 100);
        
        font = new Font("Trajan Pro", Font.PLAIN, 20);
        g2.setFont(font);
        fm = g2.getFontMetrics();
        rect = fm.getStringBounds("Back", g2);
        x = (this.getWidth() - (int) rect.getWidth()) / 2;
        g.drawString("Home", x, 30);
        
        font = new Font("Arial", Font.PLAIN, 14);
		g2.setFont(font);
        ArrayList<String> instructs = new ArrayList<String>();
        instructs.add("You are Neo in the Matrix.");
        instructs.add("To survive, use the arrow keys to dodge projectiles.");
        instructs.add("You start with 3 lives.");
        instructs.add("Balls take 1, spikes take 2, and bombs kill you instantly!");
        instructs.add("Collect hearts to gain lives.");
        instructs.add("If you have 5 lives, then you get +5 score instead.");
        instructs.add("Be careful, the game gets harder the longer you play.");
        instructs.add("");
        instructs.add("Good luck; have fun!");
        for (int i = 0; i < instructs.size(); i++) {
			int yPos = 140 + 18 * i;
			g.drawString(instructs.get(i), 30, yPos);
		}
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(COURT_WIDTH, COURT_HEIGHT);
	}
}
