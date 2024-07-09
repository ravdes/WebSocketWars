package ravdes.tictactoe.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ravdes.tictactoe.registration.confirmationtoken.ConfirmationToken;
import ravdes.tictactoe.registration.confirmationtoken.ConfirmationTokenService;
import ravdes.tictactoe.twofactorauth.TwoFactorAuthService;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Mock
	private ConfirmationTokenService confirmationTokenService;

	@Mock
	private TwoFactorAuthService twoFactorAuthService;

	@InjectMocks
	private UserService userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void shouldLoadUserByUsername() {
		UserPojo user = new UserPojo();
		user.setUsername("testuser");
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

		UserDetails userDetails = userService.loadUserByUsername("testuser");

		assertEquals("testuser", userDetails.getUsername());
	}

	@Test
	void shouldThrowUsernameDoesntExist() {
		when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

		UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
				userService.loadUserByUsername("nonexistent"));

		assertEquals("User with email nonexistent not found", exception.getMessage());
	}

	@Test
	void shouldThrowEmailExistsToUser() {
		UserPojo user = new UserPojo();
		user.setEmail("test@example.com");
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

		IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
				userService.signUpUser(user));

		assertEquals("There's already account registered with this email", exception.getMessage());
	}

	@Test
	void shouldThrowUsernameExistsToUser() {
		UserPojo user = new UserPojo();
		user.setUsername("testuser");
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

		IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
				userService.signUpUser(user));

		assertEquals("There's already account registered with this username", exception.getMessage());
	}

	@Test
	void shouldSignUpUser() {
		UserPojo user = new UserPojo();
		user.setEmail("test@example.com");
		user.setUsername("testuser");
		user.setPassword("password");

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
		when(bCryptPasswordEncoder.encode("password")).thenReturn("encodedPassword");

		String token = userService.signUpUser(user);

		ArgumentCaptor<UserPojo> userCaptor = ArgumentCaptor.forClass(UserPojo.class);
		verify(userRepository).save(userCaptor.capture());
		assertEquals("encodedPassword", userCaptor.getValue().getPassword());

		ArgumentCaptor<ConfirmationToken> tokenCaptor = ArgumentCaptor.forClass(ConfirmationToken.class);
		verify(confirmationTokenService).saveConfirmationToken(tokenCaptor.capture());
		assertEquals(token, tokenCaptor.getValue().getToken());
	}

	@Test
	void shouldThrowUsernameExistsToGuest() {
		UserPojo user = new UserPojo();
		user.setUsername("testuser");
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

		IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
				userService.signUpGuest(user));

		assertEquals("There's already account registered with this username", exception.getMessage());
	}

	@Test
	void shouldSuccessfullySignUpGuest() {
		UserPojo user = new UserPojo();
		user.setUsername("testuser");

		when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

		String result = userService.signUpGuest(user);

		verify(userRepository).save(user);
		assertEquals("good", result);
	}

	@Test
	void shouldEnableUser() {
		String email = "test@example.com";

		userService.enableUser(email);

		verify(userRepository).enableUser(email);
	}




}