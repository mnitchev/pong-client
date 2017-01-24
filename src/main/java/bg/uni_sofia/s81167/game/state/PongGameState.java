package bg.uni_sofia.s81167.game.state;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bg.uni_sofia.s81167.game.PongGame;
import bg.uni_sofia.s81167.game.model.Ball;
import bg.uni_sofia.s81167.game.model.Player;

public class PongGameState extends BasicGameState {

	private static final Logger LOGGER = LoggerFactory.getLogger(PongGameState.class);
	private static final String OK_STATUS = "OK";
	private static final String PAUSED_STATUS = "PAUSED";
	private Socket socket;
	private Player left = new Player();
	private Player right = new Player();
	private Ball ball = new Ball();
	private InputSenderThread inputSenderRunnable;
	private Thread inputSenderThread;
	private BufferedReader reader;
	private String message = "";
	private String gameId = "";
	private TextField connectToField;
	private String status;

	public PongGameState() {
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		this.connectToField = new TextField(container, container.getDefaultFont(), PongGame.WIDTH / 2 - 165,
				PongGame.HEIGHT / 2, 325, 20);
		connectToField.setBackgroundColor(Color.white);
		connectToField.setBorderColor(Color.white);
		connectToField.setTextColor(Color.black);
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		try {
			this.inputSenderRunnable = new InputSenderThread(socket, container.getInput());
			this.inputSenderThread = new Thread(inputSenderRunnable);
			this.connectToField.setText(gameId);
			inputSenderThread.start();
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			LOGGER.error("Server disconnected", e);
			throw new SlickException("Server disconnected", e);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		try {
			checkIfUserWantsToExit(container.getInput(), game);

			if (!isGameStateOK()) {
				cleanUpState(container.getInput(), game);
			} else {

			}
			getPlayerPosition(left);
			getPlayerPosition(right);
			getBallPosition();
		} catch (IOException e) {
			LOGGER.error("Server disconnected", e);
			inputSenderRunnable.stopThread();
			throw new SlickException("Server disconnected", e);
		}
	}

	private void checkIfUserWantsToExit(Input input, StateBasedGame game) {
		if (input.isKeyDown 	(Input.KEY_Q)) {
			cleanUpState(input, game);
		}
	}

	public void cleanUpState(Input input, StateBasedGame game) {
		endInputThread();
		LOGGER.debug("Entering new state");
		game.enterState(PongGame.CHOOSE_LOBBY_STATE_ID, new FadeOutTransition(), new FadeInTransition());
	}

	private void endInputThread() {
		inputSenderRunnable.stopThread();
		try {
			inputSenderThread.join();
			LOGGER.debug("Input thread stopped.");
		} catch (InterruptedException e) {
			LOGGER.warn("Interrupted input sender thread while trying to stop it");
		}
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics graphics) throws SlickException {
		graphics.drawString(message, PongGame.WIDTH / 2, 30);
		left.render(graphics);
		right.render(graphics);
		ball.render(graphics);
		if (PAUSED_STATUS.equals(status)) {
			connectToField.render(container, graphics);
			connectToField.setText(gameId);
			coppyGameId(container.getInput());
		}
		graphics.drawString("SCORE : " + left.score, PongGame.WIDTH / 2 - 200, 20);
		graphics.drawString("SCORE : " + right.score, PongGame.WIDTH / 2 + 200, 20);
	}

	private void coppyGameId(Input input) {
		if (connectToField.hasFocus() && input.isKeyDown(Input.KEY_LCONTROL) && input.isKeyDown(Input.KEY_C)) {
			StringSelection stringSelection = new StringSelection(gameId);
			Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
			clpbrd.setContents(stringSelection, stringSelection);
		}
	}

	private boolean isGameStateOK() throws IOException {
		String statusFromReader = reader.readLine();
		this.status = statusFromReader;
		return OK_STATUS.equals(statusFromReader) || PAUSED_STATUS.equals(statusFromReader);
	}

	private void getPlayerPosition(Player player) throws IOException {
		String positionX = reader.readLine();
		String positionY = reader.readLine();
		String score = reader.readLine();
		player.positionX = Integer.parseInt(positionX);
		player.positionY = Integer.parseInt(positionY);
		player.score = Integer.parseInt(score);
	}

	private void getBallPosition() throws IOException {
		String positionX = reader.readLine();
		String positionY = reader.readLine();
		ball.positionX = Integer.parseInt(positionX);
		ball.positionY = Integer.parseInt(positionY);
	}

	@Override
	public int getID() {
		return PongGame.PONG_GAME_STATE;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

}
