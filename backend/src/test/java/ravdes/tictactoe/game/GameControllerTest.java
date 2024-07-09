package ravdes.tictactoe.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ravdes.tictactoe.game.dto.GameConnectRequest;
import ravdes.tictactoe.game.entities.Game;
import ravdes.tictactoe.game.entities.GameMove;
import ravdes.tictactoe.game.entities.Player;
import ravdes.tictactoe.game.entities.PlayerMark;
import ravdes.tictactoe.jwt.JwtAuthenticationFilter;
import ravdes.tictactoe.jwt.JwtService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(GameController.class)
class GameControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private GameService gameService;

	@MockBean
	private SimpMessagingTemplate simpMessagingTemplate;

	@MockBean
	private JwtService jwtService;

	@MockBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Autowired
	private ObjectMapper objectMapper;


	@Test
	void testInitializeGame() throws Exception {
		Player player = new Player();
		Game game = new Game();
		Mockito.when(gameService.createNewGame(Mockito.any(Player.class))).thenReturn(game);

		mockMvc.perform(post("/game/create")
					   .contentType(MediaType.APPLICATION_JSON)
					   .content(objectMapper.writeValueAsString(player)))
			   .andExpect(status().isOk());
	}

	@Test
	void testConnectToExistingGame() throws Exception {
		Player player = new Player();
		player.setUsername("fakeusername");
		GameConnectRequest request = new GameConnectRequest(player, "game-id"); // Populate request as needed
		Game game = new Game();
		Mockito.when(gameService.connectToGame(Mockito.any(), Mockito.anyString())).thenReturn(game);

		mockMvc.perform(post("/game/connect")
					   .contentType(MediaType.APPLICATION_JSON)
					   .content(objectMapper.writeValueAsString(request)))
			   .andExpect(status().isOk());
	}

	@Test
	void testMakeMoveOnBoard() throws Exception {
		GameMove move = new GameMove(PlayerMark.X, 1,1,"game-id");
		Game game = new Game();
		Mockito.when(gameService.makeMove(Mockito.any(GameMove.class))).thenReturn(game);

		mockMvc.perform(post("/game/makeMove")
					   .contentType(MediaType.APPLICATION_JSON)
					   .content(objectMapper.writeValueAsString(move)))
			   .andExpect(status().isOk());
	}


}