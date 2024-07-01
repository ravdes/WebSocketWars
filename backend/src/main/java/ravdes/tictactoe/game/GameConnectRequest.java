package ravdes.tictactoe.game;

import ravdes.tictactoe.game.entities.Player;

public record GameConnectRequest(Player player, String gameId) {
}
