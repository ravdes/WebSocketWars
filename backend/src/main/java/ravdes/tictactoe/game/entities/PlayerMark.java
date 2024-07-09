package ravdes.tictactoe.game.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter

public enum PlayerMark {
	X(1),O(2);

	private final Integer value;
}
