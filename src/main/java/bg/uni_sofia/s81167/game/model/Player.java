package bg.uni_sofia.s81167.game.model;

import org.newdawn.slick.Graphics;

public class Player {

	private static final float PLAYER_WIDTH = 0;
	private static final float PLAYER_HEIGHT = 0;
	private static final int CORNER_RADIUS = 0;
	public int positionX;
	public int positionY;
	public void render(Graphics graphics) {
		graphics.drawRoundRect(positionX, positionY, PLAYER_WIDTH, PLAYER_HEIGHT, CORNER_RADIUS);
	}

}
