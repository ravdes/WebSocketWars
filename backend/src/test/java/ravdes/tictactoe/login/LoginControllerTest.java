package ravdes.tictactoe.login;

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
import ravdes.tictactoe.jwt.JwtAuthenticationFilter;
import ravdes.tictactoe.jwt.JwtService;
import ravdes.tictactoe.login.dto.LoginRequest;
import ravdes.tictactoe.login.dto.LoginResponse;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class LoginControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private LoginService loginService;

	@MockBean
	private JwtService jwtService;

	@MockBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void login_Success() throws Exception {

		LoginRequest request = new LoginRequest("user@example.com", "password");
		LoginResponse expectedResponse = new LoginResponse("user", "token", false);
		given(loginService.validateUser(request)).willReturn(expectedResponse);


		mockMvc.perform(post("/login")
					   .contentType(MediaType.APPLICATION_JSON)
					   .content(objectMapper.writeValueAsString(request)))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.username").value(expectedResponse.username()))
			   .andExpect(jsonPath("$.bearer_token").value(expectedResponse.bearer_token()))
			   .andExpect(jsonPath("$.mfaRequired").value(expectedResponse.mfaRequired()));

	}
}