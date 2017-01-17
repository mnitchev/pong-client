package bg.uni_sofia.s81167.game.state;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bg.uni_sofia.s81167.game.PongGame;

public class AuthenticationState extends BasicGameState {

	public static final String OK_RESPONSE = "OK";
	public static final String WRONG_PASSWORD_RESPONSE = "NOT_FOUND";
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationState.class);
	private static final int USERNAME_FIELD_X = PongGame.WIDTH / 2 - 50;
	private static final int USERNAME_FIELD_Y = PongGame.HEIGHT / 2 - 30;
	private static final int PASSWORD_FIELD_X = PongGame.WIDTH / 2 - 50;
	private static final int PASSWORD_FIELD_Y = PongGame.HEIGHT / 2 + 10;
	private static final int FIELD_WIDTH = 100;
	private static final int FIELD_HEIGHT = 20;

	private Socket socket;
	private TextField usernameField;
	private TextField passwordField;

	private boolean authenticated = false;

	public AuthenticationState(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		super.enter(container, game);
		usernameField.setAcceptingInput(true);
		passwordField.setAcceptingInput(true);
		authenticated = false;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		this.usernameField = new TextField(container, container.getDefaultFont(), USERNAME_FIELD_X, USERNAME_FIELD_Y,
				FIELD_WIDTH, FIELD_HEIGHT);
		this.passwordField = new TextField(container, container.getDefaultFont(), PASSWORD_FIELD_X, PASSWORD_FIELD_Y,
				FIELD_WIDTH, FIELD_HEIGHT);
		usernameField.setBackgroundColor(Color.white);
		usernameField.setBorderColor(Color.white);
		usernameField.setTextColor(Color.black);

		passwordField.setBackgroundColor(Color.white);
		passwordField.setBorderColor(Color.white);
		passwordField.setTextColor(Color.black);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics graphics) throws SlickException {
		this.usernameField.render(container, graphics);
		this.passwordField.render(container, graphics);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if (container.getInput().isKeyPressed(Input.KEY_ENTER) && !authenticated) {
			lockFields();
			String username = usernameField.getText();
			String password = passwordField.getText();
			sendAuthenticationInformation(username, password, game);
		} else if (container.getInput().isKeyPressed(Input.KEY_TAB)) {
			if (usernameField.hasFocus()) {
				passwordField.setFocus(true);
			} else {
				usernameField.setFocus(true);
			}
		}

	}

	private void lockFields() {
		usernameField.setAcceptingInput(false);
		passwordField.setAcceptingInput(false);
	}

	private void sendAuthenticationInformation(String username, String password, StateBasedGame game)
			throws SlickException {
		LOGGER.debug("Attemtping to authenticate with : " + username + " " + password);
		try {
			PrintWriter socketWriter = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			LOGGER.debug("Sending username");
			socketWriter.println(username);
			LOGGER.debug("Sending password");
			socketWriter.println(password);
			LOGGER.debug("Waiting for response");
			String response = socketReader.readLine();
			interpretResponse(response, game);
		} catch (IOException e) {
			LOGGER.error("Server disconnected!", e);
			throw new SlickException("Server disconnected!", e);
		}
	}

	private void interpretResponse(String response, StateBasedGame game) {
		LOGGER.debug("Server response : " + response);
		switch (response) {
		case OK_RESPONSE:
			authenticated = true;
			switchStateAndCleanUp(game);
			break;
		case WRONG_PASSWORD_RESPONSE:
			openFields();
			break;
		}
	}

	private void openFields() {
		usernameField.setAcceptingInput(true);
		passwordField.setAcceptingInput(true);
	}

	private void switchStateAndCleanUp(StateBasedGame game) {
		game.enterState(PongGame.CHOOSE_LOBBY_STATE_ID);
	}
	
	@Override
	public int getID() {
		return PongGame.AUTHENTICATION_STATE_ID;
	}

}
