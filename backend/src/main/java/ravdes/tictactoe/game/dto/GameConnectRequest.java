package ravdes.tictactoe.game.dto;

import ravdes.tictactoe.game.entities.Player;

public record GameConnectRequest(Player player, String gameId) {
}
