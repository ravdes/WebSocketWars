package ravdes.tictactoe.game.dto;

import ravdes.tictactoe.game.entities.PlayerMark;

public record ChatMessageRequest(String gameId, PlayerMark senderMark, String message) {
}
