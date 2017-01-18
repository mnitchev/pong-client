package bg.uni_sofia.s81167.game.model;

import org.newdawn.slick.Graphics;

public class Player {

	private static final float PLAYER_WIDTH = 20;
	private static final float PLAYER_HEIGHT = 100;
	private static final int CORNER_RADIUS = 5;
	public int positionX;
	public int positionY;
	public int score;
	
	public void render(Graphics graphics) {
		graphics.fillRoundRect(positionX, positionY, PLAYER_WIDTH, PLAYER_HEIGHT, CORNER_RADIUS);
	}

}
