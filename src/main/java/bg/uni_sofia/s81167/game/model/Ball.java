package bg.uni_sofia.s81167.game.model;

import org.newdawn.slick.Graphics;

public class Ball {

	public static final float RADIUS = 10;
	public int positionX;
	public int positionY;
	public void render(Graphics graphics) {
		graphics.drawOval(positionX, positionY, RADIUS, RADIUS);
	}

}
