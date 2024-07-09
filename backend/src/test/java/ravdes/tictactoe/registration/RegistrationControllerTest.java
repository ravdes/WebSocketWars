package ravdes.tictactoe.registration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ravdes.tictactoe.jwt.JwtAuthenticationFilter;
import ravdes.tictactoe.jwt.JwtService;
import ravdes.tictactoe.registration.dto.RegistrationRequest;
import ravdes.tictactoe.user.dto.GuestRegistrationRequest;
import ravdes.tictactoe.user.dto.GuestRegistrationResponse;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(RegistrationController.class)
class RegistrationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RegistrationService registrationService;

	@MockBean
	private JwtService jwtService;

	@MockBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Test
	void shouldRegisterUser() throws Exception {
		RegistrationRequest request = new RegistrationRequest("testUser", "testEmail@example.com", "password");
		given(registrationService.register(request)).willReturn("Successfull, check your email to verify account!");

		mockMvc.perform(post("/registration")
					   .contentType(MediaType.APPLICATION_JSON)
					   .content("{\"username\":\"testUser\",\"email\":\"testEmail@example.com\",\"password\":\"password\"}"))
			   .andExpect(status().isOk())
			   .andExpect(content().string("Successfull, check your email to verify account!"));
	}

	@Test
	void shouldConfirmToken() throws Exception {
		String token = "validToken";
		given(registrationService.confirmToken(token)).willReturn("confirmed");

		mockMvc.perform(get("/registration/confirm")
					   .param("token", token))
			   .andExpect(status().isOk())
			   .andExpect(content().string("confirmed"));
	}

	@Test
	void shouldRegisterGuest() throws Exception {
		GuestRegistrationRequest request = new GuestRegistrationRequest("guestUser");
		GuestRegistrationResponse response = new GuestRegistrationResponse("guestUser", "jwtToken");
		given(registrationService.registerGuest(request)).willReturn(response);

		mockMvc.perform(post("/registration/registerGuest")
					   .contentType(MediaType.APPLICATION_JSON)
					   .content("{\"nickname\":\"guestUser\"}"))
			   .andExpect(status().isOk());

	}

	@Test
	void shouldDeleteGuestAccount() throws Exception {

		mockMvc.perform(post("/registration/deleteGuest")
					   .contentType(MediaType.APPLICATION_JSON)
					   .content("{\"nickname\":\"guestUser\",\"bearer_token\":\"jwtToken\"}"))
			   .andExpect(status().isOk());
	}
}