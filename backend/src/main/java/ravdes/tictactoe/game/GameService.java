package ravdes.tictactoe.game;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ravdes.tictactoe.game.entities.*;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

@Service
@AllArgsConstructor

public class GameService {

	public Game createNewGame(Player player) {
		Game game = new Game();
		game.setBoard(new int[3][3]);
		game.setGameId(UUID.randomUUID().toString());
		game.setPlayer1(player);
		game.setStatus(GameStatus.CREATED);
		GameStorage.getInstance().setGame(game);
		boolean assignXToPlayer1 = new Random().nextBoolean();
		game.setPlayer1Mark(assignXToPlayer1 ? PlayerMark.X : PlayerMark.O);
		game.setPlayer2Mark(assignXToPlayer1 ? PlayerMark.O : PlayerMark.X);
		game.setTurn(PlayerMark.X);
		game.setTie(false);
		return game;
	}

	public Game getGameInfo(String gameId) {
		return GameStorage.getInstance().getGames().get(gameId);

	}



	public Game connectToGame(Player player2, String gameId) {
		if (!GameStorage.getInstance().doesGameExist(gameId)) {
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

	public Game makeMove(GameMove gameMove){
		if(!GameStorage.getInstance().doesGameExist(gameMove.getGameId())) {
			throw new IllegalStateException("Game with this gameId doesn't exist!");

		}

		Game game = GameStorage.getInstance().getGames().get(gameMove.getGameId());

		if(game.getStatus() == GameStatus.FINISHED) {
			throw new IllegalStateException("Game has already ended!");
		}

		int [][] board = game.getBoard();
		board[gameMove.getCoordinateX()][gameMove.getCoordinateY()] = gameMove.getPlayerMark().getValue();

		if (checkWinner(game.getBoard(), PlayerMark.X)) {
			game.setWinner(PlayerMark.X);
			game.setStatus(GameStatus.FINISHED);
			game.setTurn(null);
			return game;
		} else if (checkWinner(game.getBoard(), PlayerMark.O)) {
			game.setWinner(PlayerMark.O);
			game.setStatus(GameStatus.FINISHED);
			game.setTurn(null);
			return game;

		} else if (checkTie(game.getBoard())) {
			game.setStatus(GameStatus.FINISHED);
			game.setTurn(null);
			game.setTie(true);
		}
		game.setTurn(gameMove.getPlayerMark() == PlayerMark.X ? PlayerMark.O : PlayerMark.X);
		GameStorage.getInstance().setGame(game);
		return game;

	}

	public boolean checkWinner(int[][] board, PlayerMark playerMark) {
		int[] boardArray = Arrays.stream(board)
								 .flatMapToInt(Arrays::stream)
								 .toArray();

		int[][] winCombinations = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};

		return Arrays.stream(winCombinations)
					 .anyMatch(combination -> Arrays.stream(combination)
													.filter(index -> boardArray[index] == playerMark.getValue())
													.count() == 3);
	}

	public boolean checkTie(int[][] board) {
		int[] boardArray = Arrays.stream(board)
								 .flatMapToInt(Arrays::stream)
								 .toArray();
		boolean boardFilled =  Arrays.stream(boardArray).noneMatch(spot -> spot == 0);


		boolean xWins = checkWinner(board, PlayerMark.X);
		boolean oWins = checkWinner(board, PlayerMark.O);

		return boardFilled && !xWins && !oWins;

	}
}
