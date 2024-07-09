package ravdes.tictactoe.registration.confirmationtoken;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ravdes.tictactoe.user.UserPojo;
import ravdes.tictactoe.user.UserRepository;
import ravdes.tictactoe.user.UserRole;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ConfirmationTokenRepositoryTest {

	@Autowired
	private ConfirmationTokenRepository confirmationTokenRepository;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private UserRepository userRepository;

	@Test
	void shouldSaveTheToken() {
		UserPojo user = UserPojo.builder()
								.username("ravdes")
								.email("ravdes@gmail.com")
								.password("ravdes123")
								.userRole(UserRole.USER)
								.build();

		userRepository.save(user);


		ConfirmationToken token = ConfirmationToken.builder()
												   .token("token12345")
												   .createdAt(LocalDateTime.parse("2023-07-07T14:30:45"))
												   .expiresAt(LocalDateTime.parse("2023-07-07T14:45:45"))
												   .userPojo(user)
												   .build();

		ConfirmationToken savedToken = confirmationTokenRepository.save(token);

		assertThat(savedToken).isNotNull();
		assertThat(savedToken.getToken()).isEqualTo("token12345");
		assertThat(savedToken.getCreatedAt()).isEqualTo(LocalDateTime.parse("2023-07-07T14:30:45"));
		assertThat(savedToken.getExpiresAt()).isEqualTo(LocalDateTime.parse("2023-07-07T14:45:45"));
		assertThat(savedToken.getUserPojo()).isEqualTo(user);

	}

	@Test
	void shouldReturnMoreThanOneToken() {
		UserPojo user = UserPojo.builder()
								.username("ravdes")
								.email("ravdes@gmail.com")
								.password("ravdes123")
								.userRole(UserRole.USER)
								.build();
		UserPojo user2 = UserPojo.builder()
								 .username("ravdes123")
								 .email("ravdes123@gmail.com")
								 .password("ravdes123")
								 .userRole(UserRole.USER)
								 .build();
		userRepository.save(user);
		userRepository.save(user2);

		ConfirmationToken token = ConfirmationToken.builder()
												   .token("token12345")
												   .createdAt(LocalDateTime.parse("2023-07-07T14:30:45"))
												   .expiresAt(LocalDateTime.parse("2023-07-07T14:45:45"))
												   .userPojo(user)
												   .build();

		ConfirmationToken token2 = ConfirmationToken.builder()
													.token("newtoken")
													.createdAt(LocalDateTime.parse("3000-07-07T14:30:45"))
													.expiresAt(LocalDateTime.parse("3000-07-07T14:45:45"))
													.userPojo(user2)
													.build();
		confirmationTokenRepository.save(token);
		confirmationTokenRepository.save(token2);

		List<ConfirmationToken> tokenList = confirmationTokenRepository.findAll();

		assertThat(tokenList).isNotNull();
		assertThat(tokenList.size()).isEqualTo(2);
	}

	@Test
	void shouldFindTokenById() {
		UserPojo user = UserPojo.builder()
								.username("ravdes")
								.email("ravdes@gmail.com")
								.password("ravdes123")
								.userRole(UserRole.USER)
								.build();

		userRepository.save(user);


		ConfirmationToken token = ConfirmationToken.builder()
												   .token("token12345")
												   .createdAt(LocalDateTime.parse("2023-07-07T14:30:45"))
												   .expiresAt(LocalDateTime.parse("2023-07-07T14:45:45"))
												   .userPojo(user)
												   .build();

		confirmationTokenRepository.save(token);

		ConfirmationToken foundToken = confirmationTokenRepository.findById(token.getId()).get();

		assertThat(foundToken).isNotNull();

	}

	@Test
	void shouldUpdateToken() {
		UserPojo user = UserPojo.builder()
								.username("ravdes")
								.email("ravdes@gmail.com")
								.password("ravdes123")
								.userRole(UserRole.USER)
								.build();

		userRepository.save(user);


		ConfirmationToken token = ConfirmationToken.builder()
												   .token("token12345")
												   .createdAt(LocalDateTime.parse("2023-07-07T14:30:45"))
												   .expiresAt(LocalDateTime.parse("2023-07-07T14:45:45"))
												   .userPojo(user)
												   .build();

		confirmationTokenRepository.save(token);

		ConfirmationToken foundToken = confirmationTokenRepository.findById(token.getId()).get();
		foundToken.setToken("totallyNewToken");

		ConfirmationToken updatedToken = confirmationTokenRepository.save(foundToken);

		assertThat(updatedToken.getToken()).isEqualTo("totallyNewToken");

	}

	@Test
	void shouldDeleteToken() {
		UserPojo user = UserPojo.builder()
								.username("ravdes")
								.email("ravdes@gmail.com")
								.password("ravdes123")
								.userRole(UserRole.USER)
								.build();

		userRepository.save(user);


		ConfirmationToken token = ConfirmationToken.builder()
												   .token("token12345")
												   .createdAt(LocalDateTime.parse("2023-07-07T14:30:45"))
												   .expiresAt(LocalDateTime.parse("2023-07-07T14:45:45"))
												   .userPojo(user)
												   .build();

		confirmationTokenRepository.save(token);
		confirmationTokenRepository.delete(token);

		Optional<ConfirmationToken> foundToken = confirmationTokenRepository.findById(token.getId());

		assertThat(foundToken).isEmpty();


	}

	@Test
	void shouldFindTokenByToken() {
		UserPojo user = UserPojo.builder()
								.username("ravdes")
								.email("ravdes@gmail.com")
								.password("ravdes123")
								.userRole(UserRole.USER)
								.build();

		userRepository.save(user);


		ConfirmationToken token = ConfirmationToken.builder()
												   .token("token12345")
												   .createdAt(LocalDateTime.parse("2023-07-07T14:30:45"))
												   .expiresAt(LocalDateTime.parse("2023-07-07T14:45:45"))
												   .userPojo(user)
												   .build();

		confirmationTokenRepository.save(token);

		ConfirmationToken foundToken = confirmationTokenRepository.findByToken("token12345").get();

		assertThat(foundToken).isNotNull();
		assertThat(foundToken.getToken()).isEqualTo("token12345");

	}

	@Test
	void shouldUpdateTokensConfirmedAtValue() {
		UserPojo user = UserPojo.builder()
								.username("ravdes")
								.email("ravdes@gmail.com")
								.password("ravdes123")
								.userRole(UserRole.USER)
								.build();

		userRepository.save(user);


		ConfirmationToken token = ConfirmationToken.builder()
												   .token("token12345")
												   .createdAt(LocalDateTime.parse("2023-07-07T14:30:45"))
												   .expiresAt(LocalDateTime.parse("2023-07-07T14:45:45"))
												   .userPojo(user)
												   .build();

		confirmationTokenRepository.save(token);


		assertThat(token.getConfirmedAt()).isNull();

		confirmationTokenRepository.updateConfirmedAt("token12345", LocalDateTime.parse("2023-07-07T15:45:45"));

		entityManager.clear();


		ConfirmationToken foundToken = confirmationTokenRepository.findByToken("token12345").get();

		assertThat(foundToken.getConfirmedAt()).isEqualTo(LocalDateTime.parse("2023-07-07T15:45:45"));

	}

}