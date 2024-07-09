package ravdes.tictactoe.game.chat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import ravdes.tictactoe.game.GameStorage;
import ravdes.tictactoe.game.dto.ChatMessageRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

	@Mock
	private SimpMessagingTemplate simpMessagingTemplate;
	private GameStorage mockStorage;
	@InjectMocks
	private ChatService chatService;
	private MockedStatic<GameStorage> mockedStaticGameStorage;

	@BeforeEach
	void setUp() {
		mockStorage = mock(GameStorage.class);
		mockedStaticGameStorage = mockStatic(GameStorage.class);
		mockedStaticGameStorage.when(GameStorage::getInstance).thenReturn(mockStorage);
	}

	@Test
	void shouldSendMessageWhenGameExists() {

		String gameId = "existing-game-id";
		ChatMessageRequest request = new ChatMessageRequest(gameId, null, "Test message");
		when(mockStorage.doesGameExist(gameId)).thenReturn(true);

		chatService.sendMessageToPlayer(request);


		verify(simpMessagingTemplate, times(1)).convertAndSend("/topic/game-chat/" + gameId, request);
	}

	@Test
	void shouldThrowExceptionWhenGameIdDoesntExist() {

		String gameId = "non-existing-game-id";
		ChatMessageRequest request = new ChatMessageRequest(gameId, null, "Test message");
		when(GameStorage.getInstance().doesGameExist(gameId)).thenReturn(false);


		IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> chatService.sendMessageToPlayer(request));
		assertThat(thrown.getMessage()).isEqualTo("Game with this gameId doesn't exist!");

	}

	@AfterEach
	void tearDown() {
		mockedStaticGameStorage.close();
	}
}