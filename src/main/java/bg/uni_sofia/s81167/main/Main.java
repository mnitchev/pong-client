package bg.uni_sofia.s81167.main;

import java.io.IOException;
import java.net.UnknownHostException;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import bg.uni_sofia.s81167.game.PongGame;

public class Main {

	public static void main(String[] args) throws UnknownHostException, IOException {
		try {
			AppGameContainer container = new AppGameContainer(new PongGame());
			container.setDisplayMode(PongGame.WIDTH, PongGame.HEIGHT, false);
			container.setShowFPS(false);
			container.start();
		} catch (SlickException e) {
			System.out.println("Failed to start job. Application closing.");
		}

	}
}
