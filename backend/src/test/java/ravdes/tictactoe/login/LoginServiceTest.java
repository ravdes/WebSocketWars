package ravdes.tictactoe.login;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ravdes.tictactoe.jwt.JwtService;
import ravdes.tictactoe.login.dto.LoginRequest;
import ravdes.tictactoe.login.dto.LoginResponse;
import ravdes.tictactoe.user.UserPojo;
import ravdes.tictactoe.user.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Mock
	private JwtService jwtService;
	@InjectMocks
	private LoginService loginService;

	private final String userEmail = "user@example.com";
	private final String userPassword = "password";
	private final String hashedPassword = "hashedPassword";
	private final String jwtToken = "jwtToken";
	private UserPojo user;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		user = new UserPojo();
		user.setUsername("testUser");
		user.setPassword(hashedPassword);
		user.setEnabled(true);
	}

	@Test
	void shouldLoginSuccessfull() {
		when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
		when(bCryptPasswordEncoder.matches(userPassword, hashedPassword)).thenReturn(true);
		when(jwtService.generateToken(user)).thenReturn(jwtToken);

		LoginRequest request = new LoginRequest(userEmail, userPassword);
		LoginResponse response = loginService.validateUser(request);

		assertNotNull(response);
		assertEquals(user.getUsername(), response.username());
		assertEquals(jwtToken, response.bearer_token());
	}

	@Test
	void shouldThrowUserNotEnabled() {
		user.setEnabled(false);
		when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

		LoginRequest request = new LoginRequest(userEmail, userPassword);

		Exception exception = assertThrows(IllegalStateException.class, () -> loginService.validateUser(request));
		assertEquals("User is not enabled!", exception.getMessage());
	}

	@Test
	void shouldThrowIncorrectPassword() {
		when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
		when(bCryptPasswordEncoder.matches(userPassword, hashedPassword)).thenReturn(false);

		LoginRequest request = new LoginRequest(userEmail, userPassword);

		Exception exception = assertThrows(IllegalStateException.class, () -> loginService.validateUser(request));
		assertEquals("Incorrect password!", exception.getMessage());
	}

	@Test
	void shouldThrowAccountDoesntExist() {
		when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

		LoginRequest request = new LoginRequest(userEmail, userPassword);

		Exception exception = assertThrows(IllegalStateException.class, () -> loginService.validateUser(request));
		assertEquals("Account doesn't exist", exception.getMessage());
	}
}