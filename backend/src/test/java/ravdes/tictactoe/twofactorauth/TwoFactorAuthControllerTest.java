package ravdes.tictactoe.twofactorauth;

import org.junit.jupiter.api.BeforeEach;
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
import ravdes.tictactoe.user.UserService;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TwoFactorAuthController.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class TwoFactorAuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@MockBean
	private JwtService jwtService;

	@MockBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@BeforeEach
	void setUp() {
		given(userService.generateQRAndUpdateUser()).willReturn("QRCode");
		given(userService.validate2FA("validCode")).willReturn("2FA Verified");
	}

	@Test
	void enableTwoFactorAuthentication() throws Exception {
		mockMvc.perform(get("/enable2FA"))
			   .andExpect(status().isOk())
			   .andExpect(content().string("QRCode"));
	}

	@Test
	void verifyTwoFactorAuthCode() throws Exception {
		String requestBody = "{\"code\":\"validCode\"}";

		mockMvc.perform(post("/verify2FA")
					   .contentType(MediaType.APPLICATION_JSON)
					   .content(requestBody))
			   .andExpect(status().isOk())
			   .andExpect(content().string("2FA Verified"));
	}
}