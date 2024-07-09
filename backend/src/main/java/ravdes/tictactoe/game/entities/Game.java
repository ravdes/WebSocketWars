package ravdes.tictactoe.game.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Game {
	private int[][] board;
	private String gameId;
	private Player player1;
	private Player player2;
	private PlayerMark player1Mark;
	private PlayerMark player2Mark;
	private PlayerMark turn;
	private GameStatus status;
	private PlayerMark winner;
	private boolean tie;
}
