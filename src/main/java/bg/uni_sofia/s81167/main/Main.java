package bg.uni_sofia.s81167.main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import bg.uni_sofia.s81167.game.PongGame;

public class Main {

	public static void main(String[] args) throws UnknownHostException, IOException {
		try {
			Socket socket = new Socket(InetAddress.getByName(PongGame.SERVER_ADDRESS), PongGame.PORT);
			
			AppGameContainer container = new AppGameContainer(new PongGame(socket));
			container.setDisplayMode(PongGame.WIDTH, PongGame.HEIGHT, false);
			container.start();
		} catch (SlickException e) {
			System.out.println("Failed to start job. Application closing.");
		}

	}
}
