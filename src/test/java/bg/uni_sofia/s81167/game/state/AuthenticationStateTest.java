package bg.uni_sofia.s81167.game.state;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.net.Socket;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import bg.uni_sofia.s81167.game.PongGame;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationStateTest {

	public AuthenticationState authenticationState;
	
	@Mock
	public Socket socket;
	
	@Mock
	public InputStream inputStream;
	
	@Before
	public void setUp(){
		authenticationState = new AuthenticationState(socket);
	}
	
	@Test
	public void testGetId() {
		int actual = authenticationState.getID();
		assertThat(actual, is(PongGame.AUTHENTICATION_STATE_ID));
	}
	

}
