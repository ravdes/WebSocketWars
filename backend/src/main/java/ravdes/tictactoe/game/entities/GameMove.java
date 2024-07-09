package ravdes.tictactoe.game.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class GameMove {
	private PlayerMark playerMark;
	private Integer coordinateX;
	private Integer coordinateY;
	private String gameId;


}
