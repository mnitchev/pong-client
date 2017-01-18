package bg.uni_sofia.s81167.game.state;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.newdawn.slick.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputSenderThread implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(InputSenderThread.class);
	private Socket socket;
	private Input input;
	private boolean running = true;
	private PrintWriter socketWriter;

	public InputSenderThread(Socket socket, Input input) {
		this.socket = socket;
		this.input = input;
	}

	public void stopThread() {
		if (running) {
			LOGGER.error("Stopping input thread");
			this.running = false;
			socketWriter.println(Input.KEY_ESCAPE);
		}
	}

	@Override
	public void run() {
		try {
			this.socketWriter = new PrintWriter(socket.getOutputStream(), true);
			while (running) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					LOGGER.warn("Thread interrupted. This shouldn't happen, but not an major issue.", e);
				}
				processInput(socketWriter);
			}
		} catch (IOException e) {
			LOGGER.error("Connection lost.");
			running = false;
		}

	}

	private void processInput(PrintWriter socketWriter) {
		if (input.isKeyPressed(Input.KEY_UP)) {
			sendInput(socketWriter, Input.KEY_UP);
		} else if (input.isKeyPressed(Input.KEY_DOWN)) {
			sendInput(socketWriter, Input.KEY_DOWN);
		}
	}

	private void sendInput(PrintWriter socketWriter, int key) {
		socketWriter.println(key);
	}

}
