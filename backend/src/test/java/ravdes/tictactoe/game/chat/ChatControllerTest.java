package ravdes.tictactoe.game.chat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ravdes.tictactoe.game.dto.ChatMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import ravdes.tictactoe.game.entities.PlayerMark;
import ravdes.tictactoe.jwt.JwtAuthenticationFilter;
import ravdes.tictactoe.jwt.JwtService;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ChatController.class)
public class ChatControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ChatService chatService;

	@MockBean
	private JwtService jwtService;

	@MockBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void shouldSendMessage() throws Exception {
		ChatMessageRequest request = new ChatMessageRequest("game-id", PlayerMark.X, "testing");

		given(chatService.sendMessageToPlayer(request)).willReturn("Message sent");

		mockMvc.perform(post("/chat/sendMessage")
					   .contentType(MediaType.APPLICATION_JSON)
					   .content(objectMapper.writeValueAsString(request)))
			   		   .andExpect(status().isOk())
			           .andExpect(content().string("Message sent"));
	}
}