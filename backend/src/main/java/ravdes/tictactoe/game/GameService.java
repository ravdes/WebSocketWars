package ravdes.tictactoe.game;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ravdes.tictactoe.game.entities.*;
import java.util.Arrays;
import java.util.UUID;

@Service
@AllArgsConstructor

public class GameService {

	public Game createGame(Player player) {
		Game game = new Game();
		game.setBoard(new int[3][3]);
		game.setGameId(UUID.randomUUID().toString());
		game.setPlayer1(player);
		game.setStatus(GameStatus.NEW);
		GameStorage.getInstance().setGame(game);
		return game;
	}

	public Game connectToGame(Player player2, String gameId) {
		if (!GameStorage.getInstance().getGames().containsKey(gameId)) {
			throw new IllegalStateException("Game with this id doesn't exist");
		}
		Game game = GameStorage.getInstance().getGames().get(gameId);

		if(game.getPlayer2() != null) {
			throw new IllegalStateException("Game is full");

		}

		game.setPlayer2(player2);
		game.setStatus(GameStatus.IN_PROGRESS);
		GameStorage.getInstance().setGame(game);
		return game;

	}

	public Game gamePlay(GameMove gameMove){
		if(!GameStorage.getInstance().getGames().containsKey(gameMove.getGameId())) {
			throw new IllegalStateException("Game not found!");

		}

		Game game = GameStorage.getInstance().getGames().get(gameMove.getGameId());

		if(game.getStatus() == GameStatus.FINISHED) {
			throw new IllegalStateException("Game has already ended!");
		}

		int [][] board = game.getBoard();
		board[gameMove.getCoordinateX()][gameMove.getCoordinateY()] = gameMove.getPlayerMark().getValue();

		if (checkWinner(game.getBoard(), GameMark.X)) {
			game.setWinner(GameMark.X);
			game.setStatus(GameStatus.FINISHED);
		} else if (checkWinner(game.getBoard(), GameMark.O)) {
			game.setWinner(GameMark.O);
			game.setStatus(GameStatus.FINISHED);

		}

		GameStorage.getInstance().setGame(game);
		return game;

	}

	private Boolean checkWinner(int[][] board, GameMark gameMark) {
		int[] boardArray = Arrays.stream(board)
								 .flatMapToInt(Arrays::stream)
								 .toArray();

		int[][] winCombinations = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};

		return Arrays.stream(winCombinations)
					 .anyMatch(combination -> Arrays.stream(combination)
													.filter(index -> boardArray[index] == gameMark.getValue())
													.count() == 3);
	}
}
