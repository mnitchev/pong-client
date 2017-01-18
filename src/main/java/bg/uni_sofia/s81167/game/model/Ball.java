package bg.uni_sofia.s81167.game.model;

import org.newdawn.slick.Graphics;

public class Ball {

	public static final float RADIUS = 20;
	public int positionX;
	public int positionY;
	public void render(Graphics graphics) {
		graphics.fillOval(positionX, positionY, RADIUS, RADIUS);
	}

}
