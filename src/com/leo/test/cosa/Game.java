package com.leo.test.cosa;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import com.leo.test.cosa.gfx.Screen;
import com.leo.test.cosa.gfx.SpriteSheet;
import com.leo.test.cosa.level.Level;

public class Game extends Canvas implements Runnable{
	
	/**asd
	 *  Jugando con Threads :D
	 */
	private static final long serialVersionUID = 1L;
    public boolean running = false;
	private static final long Version = 1L;
	public static final int WIDTH = 160;
	public static final int HEIGHT = WIDTH / 12 * 9;
	public static final int SCALE = 5;
	public static final String NAME = "My Thread";
	public int tickCount = 0;
	
	private JFrame  frame;
	
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt)  image.getRaster().getDataBuffer()).getData();
	private int[] colours = new int[6*6*6]; // 216 colours!!
	
	private Screen screen;
	public InputHandler input;
	
	public Level level;
	
	public Game() {
		setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE ));
		setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE ));
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE ));
		frame = new JFrame(NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(this, BorderLayout.CENTER);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		}

	public void run() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000D/60D;
		int ticks = 0;
		int frames = 0;
		long lastTimer = 	System.currentTimeMillis();
	    double delta = 0;	
	init();
	while (running) {
		    long now = System.nanoTime();
		    delta += (now - lastTime) / nsPerTick;
		    lastTime = now;
		    boolean shouldRender = true;
		    while (delta >= 1){
		    	ticks++;
		    	tick();
		    	delta -= 1;
		    	shouldRender = true;
		    }
		    try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		    if (shouldRender) {
		    	frames++;
		    	render();
		    }
		    if (System.currentTimeMillis() - lastTimer > 1000){
		    	lastTimer += 1000;
		    	System.out.println("Frames: " + frames + ", " + " Ticks: " +  ticks);
		    	frames = 0;
		    	ticks = 0;
		    }
		}
	}
	
	public void init(){
		int index = 0;
		for (int r = 0; r < 6; r++){
			for (int g = 0; g < 6; g++){
				for (int b = 0; b < 6; b++){
					// I use till 255 because 256 will be my transparent color, no color.
					int rr = (r * 255 / 5); // rr stands for realRed and r for red color
					int gg = (g * 255 / 5); // gg stands for realGreen and r for green color
					int bb = (b * 255 / 5); // bb stands for realBlue and r for blue color
					colours[index++] = rr << 16 | gg << 8 | bb;
				}
			}
			
		}
		screen = new Screen(WIDTH, HEIGHT, new SpriteSheet("/sprite_sheet.png"));
		level = new Level(64, 64);
		input = new InputHandler(this);
		
	}
	
	private int x=0, y=0;
	public void tick(){
		tickCount++;
		if (input.up.isPressed()){y--;}
		if (input.down.isPressed()){y++;}
		if (input.left.isPressed()){x--;}
		if (input.right.isPressed()){x++;}
		level.tick();
	}
	
	public void render(){
		BufferStrategy bs = getBufferStrategy();
		if (bs == null){
			createBufferStrategy(3);
			return;
		}
		
		//String msg = "MiraLAmierdaquehice";
		//Font.render(msg, screen, screen.xOffset + screen.width/2  - ((msg.length()*8)/2), screen.yOffset + screen.height/2, Colours.get(-1, -1, -1, 000));
		int xOffset = x - (screen.width /2);
		int yOffset= y - (screen.height/2);
		level.renderTiles(screen, xOffset, yOffset);
		for (int y =0;  y < screen.height; y++){
			for (int x =0;  x < screen.width; x++){
				int colorCode = screen.pixels[x + y * screen.width];
				if (colorCode < 255) pixels[x + y * WIDTH] = colours[colorCode];
				
			}
		}
		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g.dispose();
		bs.show();
	}
	
	public static long getVersion() {
		return Version;
	}
	
	public synchronized void Start(){
		running = true;
		new Thread(this).start();	
	}

	public synchronized void Stop(){
		running = false;
	}
	
	public static void main(String[] args){
		new Game().Start();
	}

}
