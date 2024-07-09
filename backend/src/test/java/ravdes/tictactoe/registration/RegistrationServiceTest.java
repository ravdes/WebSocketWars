package ravdes.tictactoe.registration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ravdes.tictactoe.emailsending.EmailSender;
import ravdes.tictactoe.jwt.JwtService;
import ravdes.tictactoe.registration.confirmationtoken.ConfirmationToken;
import ravdes.tictactoe.registration.confirmationtoken.ConfirmationTokenService;
import ravdes.tictactoe.registration.dto.RegistrationRequest;
import ravdes.tictactoe.user.UserPojo;
import ravdes.tictactoe.user.UserRepository;
import ravdes.tictactoe.user.UserRole;
import ravdes.tictactoe.user.UserService;
import ravdes.tictactoe.user.dto.GuestRegistrationRequest;
import ravdes.tictactoe.user.dto.GuestRegistrationResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;


class RegistrationServiceTest {

	@InjectMocks
	private RegistrationService registrationService;

	@Mock
	private EmailVerifier emailVerifier;

	@Mock
	private UserService userService;

	@Mock
	private ConfirmationTokenService confirmationTokenService;

	@Mock
	private EmailSender emailSender;

	@Mock
	private JwtService jwtService;

	@Mock
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void shouldRegisterUser() {
		RegistrationRequest request = new RegistrationRequest("testUser", "testEmail@example.com", "password");
		when(emailVerifier.validateEmail(request.email())).thenReturn(true);
		when(userService.signUpUser(any(UserPojo.class))).thenReturn("token");

		String result = registrationService.register(request);

		assertEquals("Successfull, check your email to verify account!", result);
		verify(emailSender, times(1)).send(eq(request.email()), anyString());
	}

	@Test
	void shouldThrowExceptionInvalidEmail() {
		RegistrationRequest request = new RegistrationRequest("testUser", "invalidEmail", "password");
		when(emailVerifier.validateEmail(request.email())).thenReturn(false);

		Exception exception = assertThrows(IllegalStateException.class, () -> registrationService.register(request));
		assertThat(exception.getMessage()).isEqualTo("email not valid");
	}

	@Test
	void shouldCreateGuestAccountAndReturnJwt() {
		GuestRegistrationRequest request = new GuestRegistrationRequest("guestUser");
		when(jwtService.generateToken(any(UserPojo.class))).thenReturn("jwtToken");

		GuestRegistrationResponse response = registrationService.registerGuest(request);

		assertEquals("guestUser", response.nickname());
		assertEquals("jwtToken", response.bearer_token());
		verify(userService, times(1)).signUpGuest(any(UserPojo.class));
	}

	@Test
	void shouldDeleteGuestAndInvalidateJwt() {
		GuestRegistrationResponse request = new GuestRegistrationResponse("guestUser", "jwtToken");
		when(userRepository.findByUsername(request.nickname())).thenReturn(Optional.of(new UserPojo()));

		registrationService.deleteGuest(request);

		verify(userRepository, times(1)).delete(any(UserPojo.class));
		verify(jwtService, times(1)).addToBlacklist(request.bearer_token());
	}

	@Test
	void shouldConfirmUserWithValidToken() {
		String token = "validToken";
		ConfirmationToken confirmationToken = new ConfirmationToken();
		confirmationToken.setUserPojo(new UserPojo("testUser", "testEmail@example.com", "password", UserRole.USER));
		confirmationToken.setExpiresAt(LocalDateTime.now().plusDays(1));
		when(confirmationTokenService.getToken(token)).thenReturn(Optional.of(confirmationToken));

		String result = registrationService.confirmToken(token);

		assertEquals("confirmed", result);
		verify(confirmationTokenService, times(1)).setConfirmedAt(token);
		verify(userService, times(1)).enableUser("testEmail@example.com");
	}

	@Test
	void shouldThrowExceptionIfTokenInvalid() {
		when(confirmationTokenService.getToken("invalidToken")).thenReturn(Optional.empty());

		Exception exception = assertThrows(IllegalStateException.class, () -> registrationService.confirmToken("invalidToken"));
		assertThat(exception.getMessage()).isEqualTo("token not found");
	}

	@Test
	void shouldThrowExceptionIfConfirmationTokenExpired() {
		String token = "expiredToken";
		ConfirmationToken confirmationToken = new ConfirmationToken();
		confirmationToken.setExpiresAt(LocalDateTime.now().minusDays(1));
		when(confirmationTokenService.getToken(token)).thenReturn(Optional.of(confirmationToken));

		Exception exception = assertThrows(IllegalStateException.class, () -> registrationService.confirmToken(token));
		assertThat(exception.getMessage()).isEqualTo("token expired");
	}

	@Test
	void shouldCheckIfConfirmationTokenAlreadyConfirmed() {
		String token = "alreadyConfirmedToken";
		ConfirmationToken confirmationToken = new ConfirmationToken();
		confirmationToken.setConfirmedAt(LocalDateTime.now());
		when(confirmationTokenService.getToken(token)).thenReturn(Optional.of(confirmationToken));

		Exception exception = assertThrows(IllegalStateException.class, () -> registrationService.confirmToken(token));
		assertThat(exception.getMessage()).isEqualTo("email already confirmed");
	}
}