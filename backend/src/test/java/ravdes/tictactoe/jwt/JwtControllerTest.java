package ravdes.tictactoe.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ravdes.tictactoe.jwt.dto.BlacklistTokenRequest;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(JwtController.class)
class JwtControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private JwtService jwtService;

	@MockBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void testBlacklistToken() throws Exception {
		BlacklistTokenRequest request = new BlacklistTokenRequest("someBearerToken");

		mockMvc.perform(post("/blacklistToken")
					   .contentType(MediaType.APPLICATION_JSON)
					   .content(objectMapper.writeValueAsString(request)))
			   .andExpect(status().isOk());

		verify(jwtService).addToBlacklist("someBearerToken");
	}

}