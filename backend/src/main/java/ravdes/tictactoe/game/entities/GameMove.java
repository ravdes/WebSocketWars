package ravdes.tictactoe.game.entities;

import lombok.Data;

@Data

public class GameMove {
	private GameMark playerMark;
	private Integer coordinateX;
	private Integer coordinateY;
	private String gameId;
}
