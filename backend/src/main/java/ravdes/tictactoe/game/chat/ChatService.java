package ravdes.tictactoe.game.chat;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ravdes.tictactoe.game.GameStorage;
import ravdes.tictactoe.game.dto.ChatMessageRequest;

@Service

public class ChatService {
	private final SimpMessagingTemplate simpMessagingTemplate;

	public ChatService(SimpMessagingTemplate simpMessagingTemplate) {
		this.simpMessagingTemplate = simpMessagingTemplate;
	}

	public String sendMessageToPlayer(ChatMessageRequest chatMessageRequest) {
		if (!GameStorage.getInstance().doesGameExist(chatMessageRequest.gameId())) {
			throw new IllegalStateException("Game with this gameId doesn't exist!");

		}

		simpMessagingTemplate.convertAndSend("/topic/game-chat/" + chatMessageRequest.gameId(), chatMessageRequest);
		return "Successfully send message";
	}
}
