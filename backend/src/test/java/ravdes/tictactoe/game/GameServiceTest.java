package ravdes.tictactoe.game;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ravdes.tictactoe.game.entities.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {
	@Mock
	private GameStorage gameStorage;
	@InjectMocks
	private GameService gameService;

	private MockedStatic<GameStorage> mockedStatic;


	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockedStatic = mockStatic(GameStorage.class);
		when(GameStorage.getInstance()).thenReturn(gameStorage);
	}

	@AfterEach
	void tearDown() {
		mockedStatic.close();
	}


	@Test
	void shouldCreateNewGame() {
		Player player = new Player();
		player.setUsername("testplayer");

		Game game = gameService.createNewGame(player);

		assertThat(game.getBoard()).isNotNull();
		assertThat(game.getBoard().length).isEqualTo(3);
		assertThat(game.getGameId()).isNotNull();
		assertThat(game.getPlayer1().getUsername()).isEqualTo("testplayer");
		assertThat(game.getStatus()).isEqualTo(GameStatus.CREATED);
		assertThat(game.getPlayer1Mark()).isNotNull();
		assertThat(game.getPlayer2Mark()).isNotNull();
		assertThat(game.getTurn()).isEqualTo(PlayerMark.X);


	}

	@Test
	void shouldThrowGameDoesntExist() {
		Player player = new Player();
		player.setUsername("testplayer123");

		String gameId = "test-game-id";

		when(gameStorage.doesGameExist(gameId)).thenReturn(false);
		Exception exception = assertThrows(IllegalStateException.class,
				() -> gameService.connectToGame(player, gameId));
		assertThat(exception.getMessage()).isEqualTo("Game with this id doesn't exist");
	}

	@Test
	void shouldConnectToGame() {
		String gameId = UUID.randomUUID().toString();
		Game game = new Game();
		game.setStatus(GameStatus.CREATED);
		Player player2 = new Player();
		player2.setUsername("player2");

		Map<String, Game> gamesMap = new HashMap<>();
		gamesMap.put(gameId, game);

		when(gameStorage.doesGameExist(gameId)).thenReturn(true);
		when(gameStorage.getGames()).thenReturn(gamesMap);

		Game updatedGame = gameService.connectToGame(player2, gameId);


		assertThat(updatedGame.getStatus()).isEqualTo(GameStatus.IN_PROGRESS);
		assertThat(updatedGame.getPlayer2()).isEqualTo(player2);


	}

	@Test
	void shouldThrowGameIsFull() {
		String gameId = UUID.randomUUID().toString();
		Game game = mock(Game.class);

		Player player2 = new Player();
		player2.setUsername("player 2");

		when(game.getPlayer2()).thenReturn(player2);

		Map<String, Game> gamesMap = new HashMap<>();
		gamesMap.put(gameId, game);

		when(gameStorage.getGames()).thenReturn(gamesMap);
		when(gameStorage.doesGameExist(gameId)).thenReturn(true);

		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			gameService.connectToGame(player2, gameId);
		});

		assertThat(exception.getMessage()).isEqualTo("Game is full");
	}

	@Test
	void shouldUpdateBoardIfMoveValid() {
		String gameId = UUID.randomUUID().toString();
		Game game = new Game();
		game.setGameId(gameId);
		game.setBoard(new int[3][3]);
		game.setStatus(GameStatus.IN_PROGRESS);
		game.setTurn(PlayerMark.X);
		GameMove gameMove = new GameMove(PlayerMark.X, 1, 1, gameId);

		Map<String, Game> gamesMap = new HashMap<>();
		gamesMap.put(gameId, game);


		when(gameStorage.doesGameExist(gameId)).thenReturn(true);
		when(gameStorage.getGames()).thenReturn(gamesMap);

		Game updatedGame = gameService.makeMove(gameMove);


		assertThat(updatedGame.getBoard()[1][1]).isEqualTo(PlayerMark.X.getValue());

	}


	@Test
	void shouldThrowExceptionIfMakingMoveOnFinishedGame() {
		String gameId = UUID.randomUUID().toString();
		Game game = new Game();
		game.setStatus(GameStatus.FINISHED);
		GameMove gameMove = new GameMove(PlayerMark.X, 1, 1, gameId);

		Map<String, Game> gamesMap = new HashMap<>();
		gamesMap.put(gameId, game);

		when(gameStorage.doesGameExist(gameId)).thenReturn(true);
		when(gameStorage.getGames()).thenReturn(gamesMap);
		Exception exception = assertThrows(IllegalStateException.class, () -> gameService.makeMove(gameMove));

		assertThat(exception.getMessage()).isEqualTo("Game has already ended!");
	}


	@Test
	void shouldGiveWinFalse() {
		int[][] board = {
				{0, 0, 0},
				{0, 0, 0},
				{0, 0, 0}
		};

		assertFalse(gameService.checkWinner(board, PlayerMark.X));
	}

	@Test
	void shouldGiveWinTrue() {
		int[][] board = {
				{1, 0, 0},
				{0, 1, 0},
				{0, 0, 1}
		};

		assertTrue(gameService.checkWinner(board, PlayerMark.X));
	}

	@Test
	void shouldGiveTieFalse() {
		int[][] board = {
				{PlayerMark.X.getValue(), PlayerMark.O.getValue(), 0},
				{PlayerMark.O.getValue(), PlayerMark.X.getValue(), PlayerMark.X.getValue()},
				{0, PlayerMark.O.getValue(), PlayerMark.X.getValue()}
		};

		assertFalse(gameService.checkTie(board));
	}

	@Test
	void shouldGiveTieTrue() {
		int[][] board = {
				{PlayerMark.X.getValue(), PlayerMark.O.getValue(), PlayerMark.X.getValue()},
				{PlayerMark.O.getValue(), PlayerMark.X.getValue(), PlayerMark.X.getValue()},
				{PlayerMark.O.getValue(), PlayerMark.X.getValue(), PlayerMark.O.getValue()}
		};

		assertTrue(gameService.checkTie(board));
	}

	@Test
	void shouldReturnCorrectGame() {
		String gameId = UUID.randomUUID().toString();
		Game expectedGame = new Game();
		Map<String, Game> gamesMap = new HashMap<>();
		gamesMap.put(gameId, expectedGame);

		when(gameStorage.getGames()).thenReturn(gamesMap);

		Game actualGame = gameService.getGameInfo(gameId);

		assertEquals(expectedGame, actualGame);
	}

}