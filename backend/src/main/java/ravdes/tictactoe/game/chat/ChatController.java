package ravdes.tictactoe.game.chat;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ravdes.tictactoe.game.dto.ChatMessageRequest;

@RestController
@RequestMapping("/chat")

public class ChatController {
	private final ChatService chatService;

	public ChatController(ChatService chatService) {
		this.chatService = chatService;
	}

	@PostMapping("/sendMessage")
	public String sendMessage(@RequestBody ChatMessageRequest request) {
		return chatService.sendMessageToPlayer(request);
	}
}
