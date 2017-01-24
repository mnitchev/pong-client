package bg.uni_sofia.s81167.game;

import java.net.Socket;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import bg.uni_sofia.s81167.game.state.AuthenticationState;
import bg.uni_sofia.s81167.game.state.ChooseLobbyState;
import bg.uni_sofia.s81167.game.state.PongGameState;

public class PongGame extends StateBasedGame {

	public static final int HEIGHT = 768;
	public static final int WIDTH = 1024;
	public static final int AUTHENTICATION_STATE_ID = 1;
	public static final int CHOOSE_LOBBY_STATE_ID = 2;
	public static final int PONG_GAME_STATE = 3;
	public static final int WAIT_FOR_PLAYER_STATE_ID = 4;
	public static final int PORT = 10514;
	public static final String SERVER_ADDRESS = "127.0.0.1";


	public PongGame() {
		super("Pong");
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		this.addState(new AuthenticationState());
		this.addState(new ChooseLobbyState());
		this.addState(new PongGameState());
		this.enterState(AUTHENTICATION_STATE_ID);
	}
}
