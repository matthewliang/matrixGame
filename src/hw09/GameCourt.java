package hw09;
/**
 * CIS 120 Game HW
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */

import java.awt.*;
import java.awt.event.*;
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

	public boolean playing = false; // whether the game is running
	private JLabel status; // Current status text (i.e. Running...)

	// Game constants
	public static final int COURT_WIDTH = 400;
	public static final int COURT_HEIGHT = 415;
	// Update interval for timer, in milliseconds
	public static final int INTERVAL = 35;
	public static final int GRACE_TIME = 15;
	public static final int INIT_SPIKE_PROB = 1;
	public static final int INIT_BOMB_PROB = 0;
	
	public static int score = 0;
	
	public int counter = 0;
	public int graceTimer = 0;
	
	public static int spikeProb = INIT_SPIKE_PROB;
	public static int bombProb = INIT_BOMB_PROB;
	
	public static final String img_file = "heartSmall.png";
	private static BufferedImage img;
	
	public static final String highscores_file = "highscores.txt";
	
	public static Random rand = new Random();
	
	public static ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	public static ArrayList<GameObj> bonuses = new ArrayList<GameObj>();

	public GameCourt(JLabel status) {
		try {
			if (img == null) {
				img = ImageIO.read(new File(img_file));
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

		this.status = status;
	}

	/**
	 * (Re-)set the game to its initial state.
	 */
	public void reset() {

		neo = new Neo(COURT_WIDTH, COURT_HEIGHT);
		projectiles = new ArrayList<Projectile>();
		bonuses = new ArrayList<GameObj>();

		playing = true;
		score = 0;
		spikeProb = INIT_SPIKE_PROB;
		bombProb = INIT_BOMB_PROB;
		status.setText("Score: " + score);

		// Make sure that this component has the keyboard focus
		requestFocusInWindow();
	}

	/**
	 * This method is called every time the timer defined in the constructor
	 * triggers.
	 */
	void tick() {
		if (playing) {
			
			int heartSpawn = rand.nextInt(100);
			if (heartSpawn < 1) {
				addHeart();
			}
			java.util.Iterator<GameObj> bonusIter = bonuses.iterator();
			while (bonusIter.hasNext()) {
				GameObj b = bonusIter.next();
				if (b.intersects(neo)) {
					b.interact(neo);
					bonusIter.remove();
				}
			}
			
			if (counter % 10 == 0) {
				score++;
				addProjectile();
			}
			
			java.util.Iterator<Projectile> projIter = projectiles.iterator();
			while (projIter.hasNext()) {
				Projectile p = projIter.next();
				p.move();
				
				if (!neo.invulnerable) {
					if (p.intersects(neo)) {
						p.interact(neo);
						if (neo.lives <= 0) {
							playing = false;
						}
						neo.invulnerable = true;
						System.out.println("Hit by " + p);
						if (playing) {
							projIter.remove();
						}
					}
				}

				if (p.pos_x < 0 || p.pos_y < 0 || p.pos_x > 400 || p.pos_y > 400) {
					projIter.remove();
				}
			}
			
			status.setText("Score: " + score);
			
			counter++;
			
			if (neo.invulnerable) {
				graceTimer++;
				if (graceTimer == GRACE_TIME) {
					neo.invulnerable = false;
					graceTimer = 0;
				}
			}
			
			if (counter % 500 == 0) {
				spikeProb ++;
				bombProb ++;
			}
			
			// update the display
			repaint();
			
			if (!playing) {
				try {
					String playerName = getPlayerName("");
					if (playerName != null) {
						addHighScore(playerName, score);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void changeScore(int x) {
		score += x;
	}

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
	    
	    if(playerName.isEmpty() || !playerName.matches("[A-Za-z]*"))
	    {
	        playerName = getPlayerName("Please enter a name that only contains letters.\n");
	    }

	    return playerName;
	}
	
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
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 400, 400);
		g.setColor(Color.BLACK);
		g.drawRect(50, 50, 300, 300);
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
			g.drawImage(subImg, heartXPos, 385, heartWidth, 10, null);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(COURT_WIDTH, COURT_HEIGHT);
	}
}
