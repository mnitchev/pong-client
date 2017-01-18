package bg.uni_sofia.s81167.game.state;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bg.uni_sofia.s81167.game.PongGame;

public class ChooseLobbyState extends BasicGameState {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChooseLobbyState.class);
	private static final String OK_RESPONSE = "OK";
	private static final String NEW_GAME_REQUEST = "NEW_GAME";
	private Socket socket;
	private TextField idField;
	private Image button;
	private String gameId;
	private String message = "";
	private boolean gameCreated = false;

	public ChooseLobbyState() {
	}
	
	public void setSocket(Socket socket){
		this.socket = socket;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		idField = new TextField(container, container.getDefaultFont(), PongGame.WIDTH / 2 - 100,
				PongGame.HEIGHT / 2 - 20, 200, 20);
		idField.setBackgroundColor(Color.white);
		idField.setBorderColor(Color.white);
		idField.setTextColor(Color.black);
		button = new Image("images/button.png");
		LOGGER.debug("Initialized lobby state");
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		LOGGER.debug("Entered Choose lobby game state");
		idField.setAcceptingInput(true);
		gameCreated = false;
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics graphics) throws SlickException {
		graphics.drawString(message, 30, 30);
		idField.render(container, graphics);
		button.draw(PongGame.WIDTH / 2 - 100, PongGame.HEIGHT / 2 + 20);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if (container.getInput().isKeyPressed(Input.KEY_ENTER) && !gameCreated) {
			String requestedGameId = idField.getText();
			idField.setAcceptingInput(false);
			connectToGame(requestedGameId, game);
		}
		if(container.getInput().isKeyPressed(Input.KEY_TAB)){
			idField.setFocus(true);
		}
		if (isMouseOnButton() && !gameCreated) {
			if (isButtonPressed()) {
				gameCreated = true;
				sendNewGameRequest(game);
			}
		}
	}

	private void connectToGame(String requestedGameId, StateBasedGame game) throws SlickException {
		try {
			PrintWriter socketWriter = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			LOGGER.debug("Sending request for game : " + requestedGameId);
			socketWriter.println(requestedGameId);

			String response = socketReader.readLine();
			LOGGER.debug("Received response : " + response);

			interpreteResponse(game, socketReader, response);
		} catch (IOException e) {
			LOGGER.error("Server disconnected!", e);
			throw new SlickException("Server disconnected", e);
		}
	}

	private void interpreteResponse(StateBasedGame game, BufferedReader socketReader, String response)
			throws IOException {
		if (OK_RESPONSE.equals(response)) {
			gameId = socketReader.readLine();
			LOGGER.debug("Received gameid = {}", gameId);
			message = "";
			PongGameState gameState = (PongGameState) game.getState(PongGame.PONG_GAME_STATE);
			gameState.setGameId(gameId);
			LOGGER.debug("Set PongGameState id and now switching state.");
			game.enterState(PongGame.PONG_GAME_STATE);
		} else {
			gameCreated = false;
			idField.setAcceptingInput(true);
			message = "Invalid game id.";
		}
	}

	private boolean isButtonPressed() {
		return Mouse.isButtonDown(Input.MOUSE_LEFT_BUTTON);
	}

	private boolean isMouseOnButton() {
		int mouseX = Mouse.getX();
		int mouseY = Mouse.getY();
		return mouseX >= PongGame.WIDTH / 2 - 100 && mouseX <= PongGame.WIDTH / 2 + 100
				&& mouseY >= PongGame.HEIGHT / 2 - 75 && mouseY <= PongGame.HEIGHT / 2 - 25;
	}

	private void sendNewGameRequest(StateBasedGame game) throws SlickException {
		connectToGame(NEW_GAME_REQUEST, game);
	}

	@Override
	public int getID() {
		return PongGame.CHOOSE_LOBBY_STATE_ID;
	}

}
