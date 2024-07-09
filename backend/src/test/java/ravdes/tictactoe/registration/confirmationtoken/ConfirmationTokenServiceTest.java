package ravdes.tictactoe.registration.confirmationtoken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ConfirmationTokenServiceTest {

	@InjectMocks
	private ConfirmationTokenService confirmationTokenService;

	@Mock
	private ConfirmationTokenRepository confirmationTokenRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void shouldSaveToken() {
		ConfirmationToken token = new ConfirmationToken();
		when(confirmationTokenRepository.save(token)).thenReturn(token);

		confirmationTokenService.saveConfirmationToken(token);

		verify(confirmationTokenRepository, times(1)).save(token);
	}

	@Test
	void shouldReturnToken() {
		String tokenValue = "token123";
		ConfirmationToken token = new ConfirmationToken();
		when(confirmationTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));

		Optional<ConfirmationToken> foundToken = confirmationTokenService.getToken(tokenValue);

		assertTrue(foundToken.isPresent());
		assertEquals(token, foundToken.get());
	}

	@Test
	void shouldReturnEmptyIfTokenDoesntExist() {
		String tokenValue = "nonexistentToken";
		when(confirmationTokenRepository.findByToken(tokenValue)).thenReturn(Optional.empty());

		Optional<ConfirmationToken> foundToken = confirmationTokenService.getToken(tokenValue);

		assertFalse(foundToken.isPresent());
	}

	@Test
	void shouldUpdateConfirmedAtDate() {
		String tokenValue = "token123";
		LocalDateTime now = LocalDateTime.now();
		doNothing().when(confirmationTokenRepository).updateConfirmedAt(tokenValue, now);

		confirmationTokenService.setConfirmedAt(tokenValue);

		verify(confirmationTokenRepository, times(1)).updateConfirmedAt(eq(tokenValue), any(LocalDateTime.class));
	}
}