package bg.uni_sofia.s81167.game.state;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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
	private TextField serverNameField;
	private TextField usernameField;
	private TextField passwordField;

	private boolean authenticated = false;
	private String message = "";
	private boolean alreadyConnected = false;

	public AuthenticationState() {
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
		this.serverNameField = new TextField(container, container.getDefaultFont(), USERNAME_FIELD_X,
				USERNAME_FIELD_Y - 40, FIELD_WIDTH, FIELD_HEIGHT);
		serverNameField.setMaxLength(15);
		usernameField.setMaxLength(25);
		passwordField.setMaxLength(10);
		setInputField(serverNameField);
		setInputField(usernameField);
		setInputField(passwordField);
	}

	private void setInputField(TextField field) {
		field.setBackgroundColor(Color.white);
		field.setBorderColor(Color.white);
		field.setTextColor(Color.black);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics graphics) throws SlickException {
		graphics.drawString("Login or Register", PongGame.WIDTH / 2 - 80, PongGame.HEIGHT / 2 - 95);
		this.serverNameField.render(container, graphics);
		this.usernameField.render(container, graphics);
		this.passwordField.render(container, graphics);
		graphics.drawString(message, PongGame.WIDTH / 2 - 110, PongGame.HEIGHT / 2 + 35);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if (container.getInput().isKeyPressed(Input.KEY_ENTER) && !authenticated) {
			lockFields();
			if (!alreadyConnected) {
				String serverAddress = serverNameField.getText();
				connectToServer(serverAddress, game);
			}
			if (alreadyConnected) {
				String username = usernameField.getText();
				String password = passwordField.getText();
				if (!username.isEmpty() && !password.isEmpty()) {
					sendAuthenticationInformation(username, password, game);
				}else{
					openFields();
					message = "Username or password is empty";
				}
			}
		} else if (container.getInput().isKeyPressed(Input.KEY_TAB)) {
			setNextFocus();
		}

	}

	private void setNextFocus() {
		if (serverNameField.hasFocus()) {
			usernameField.setFocus(true);
		} else {
			if (usernameField.hasFocus()) {
				passwordField.setFocus(true);
			} else {
				serverNameField.setFocus(true);
			}
		}
	}

	private void connectToServer(String serverAddress, StateBasedGame game) {
		try {
			LOGGER.debug("Connecting");
			Socket socketConnection = new Socket();
			socketConnection.connect(new InetSocketAddress(InetAddress.getByName(serverAddress), PongGame.PORT), 2000);
			this.socket = socketConnection;
			serverNameField.setAcceptingInput(false);
			alreadyConnected = true;
			setSocketsOnOtherStates(game);
		} catch (IOException e) {
			LOGGER.debug("Server not found", e);
			message = "    Server not found";
			openFields();
		}
	}

	private void setSocketsOnOtherStates(StateBasedGame game) {
		ChooseLobbyState lobbyState = (ChooseLobbyState) game.getState(PongGame.CHOOSE_LOBBY_STATE_ID);
		lobbyState.setSocket(socket);
		PongGameState gameState = (PongGameState) game.getState(PongGame.PONG_GAME_STATE);
		gameState.setSocket(socket);
	}

	private void lockFields() {
		usernameField.setAcceptingInput(false);
		passwordField.setAcceptingInput(false);
	}

	private void sendAuthenticationInformation(String username, String password, StateBasedGame game)
			throws SlickException {
		try {
			PrintWriter socketWriter = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			socketWriter.println(username);
			socketWriter.println(password);
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
			message = "";
			switchState(game);
			break;
		case WRONG_PASSWORD_RESPONSE:
			message = "Wrong username or password";
			openFields();
			break;
		}
	}

	private void openFields() {
		usernameField.setAcceptingInput(true);
		passwordField.setAcceptingInput(true);
	}

	private void switchState(StateBasedGame game) {
		game.enterState(PongGame.CHOOSE_LOBBY_STATE_ID);
	}

	@Override
	public int getID() {
		return PongGame.AUTHENTICATION_STATE_ID;
	}

}
