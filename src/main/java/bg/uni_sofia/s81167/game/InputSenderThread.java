package bg.uni_sofia.s81167.game;

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

	public InputSenderThread(Socket socket, Input input) {
		this.socket = socket;
		this.input = input;
	}

	public void stopThread() {
		this.running = false;
	}

	@Override
	public void run() {
		try{
			PrintWriter socketWriter = new PrintWriter(socket.getOutputStream(), true);
			while (running) {
				processInput(socketWriter);
			}
		} catch (IOException e) {
			LOGGER.error("Connection lost.");
			running = false;
		}

	}

	private void processInput(PrintWriter socketWriter) {
		if (input.isKeyDown(Input.KEY_UP)) {
			sendInput(socketWriter, Input.KEY_UP);
		} else if (input.isKeyDown(Input.KEY_DOWN)) {
			sendInput(socketWriter, Input.KEY_DOWN);
		} else if (input.isKeyDown(Input.KEY_SPACE)) {
			sendInput(socketWriter, Input.KEY_SPACE);
		}
	}

	private void sendInput(PrintWriter socketWriter, int key) {
		socketWriter.print(key);
	}

}
