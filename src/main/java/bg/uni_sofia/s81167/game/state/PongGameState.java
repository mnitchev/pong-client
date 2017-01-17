package bg.uni_sofia.s81167.game.state;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bg.uni_sofia.s81167.game.InputSenderThread;
import bg.uni_sofia.s81167.game.PongGame;
import bg.uni_sofia.s81167.game.model.Ball;
import bg.uni_sofia.s81167.game.model.Player;

public class PongGameState extends BasicGameState{

	private static final Logger LOGGER = LoggerFactory.getLogger(PongGameState.class);
	private static final String OK_STATUS = "OK";
	private Socket socket;
	private Player left;
	private Player right;
	private Ball ball;
	private Thread inputSenderThread;
	private BufferedReader reader;
	private String message = "";
	private String gameId;
	
	
	public PongGameState(Socket socket){
		this.socket = socket;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		try {
			this.inputSenderThread = new Thread(new InputSenderThread(socket, container.getInput()));
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
			if(!isGameStateOK()){
				activateDisconnectedMessage();
			}else{
				deactivateDisconnectedMessage();
			}
			getPlayerPosition(left);
			getPlayerPosition(right);
			getBallPosition();
			getScore();
		} catch (IOException e) {
			LOGGER.error("Server disconnected", e);
			inputSenderThread.interrupt(); //TODO: change this later.
			throw new SlickException("Server disconnected", e);
		}
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics graphics) throws SlickException {
		graphics.drawString(message, PongGame.WIDTH / 2, 30);
		left.render(graphics);
		right.render(graphics);
		ball.render(graphics);
	}

	private void activateDisconnectedMessage() {
		message = gameId;
	}
	
	private void deactivateDisconnectedMessage() {
		message = "";
	}

	private boolean isGameStateOK() throws IOException {
		String status = reader.readLine();
		return OK_STATUS.equals(status);
	}

	private void getPlayerPosition(Player player) throws IOException {
		String positionX = reader.readLine();
		String positionY = reader.readLine();
		player.positionX = Integer.parseInt(positionX);
		player.positionY = Integer.parseInt(positionY);
	}


	private void getBallPosition() throws IOException {
		String positionX = reader.readLine();
		String positionY = reader.readLine();
		ball.positionX = Integer.parseInt(positionX);
		ball.positionY = Integer.parseInt(positionY);
	}

	private void getScore() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getID() {
		return PongGame.PONG_GAME_STATE;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

}
