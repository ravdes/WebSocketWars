package ravdes.tictactoe.user;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserRepositoryTest {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private UserRepository userRepository;


	@Test
	void shouldSaveTheUser() {
		UserPojo user = UserPojo.builder()
								.username("ravdes")
								.email("ravdes@gmail.com")
								.password("ravdes123")
								.userRole(UserRole.USER)
								.build();

		UserPojo savedUser = userRepository.save(user);

		assertThat(savedUser).isNotNull();
		assertThat(savedUser.getId()).isPositive();
		assertThat(savedUser.getUsername()).isEqualTo("ravdes");
		assertThat(savedUser.getEmail()).isEqualTo("ravdes@gmail.com");
		assertThat(savedUser.getPassword()).isEqualTo("ravdes123");
		assertThat(savedUser.getUserRole()).isEqualTo(UserRole.USER);
		assertThat(savedUser.getLocked()).isFalse();
		assertThat(savedUser.getEnabled()).isFalse();
		assertThat(savedUser.getMfa()).isFalse();
		assertThat(savedUser.getSecret()).isEmpty();
	}

	@Test
	void shouldReturnMoreThanOneUser() {
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
		List<UserPojo> userList = userRepository.findAll();

		assertThat(userList).isNotNull();
		assertThat(userList.size()).isEqualTo(2);
	}

	@Test
	void shouldFindUserById() {
		UserPojo user = UserPojo.builder()
								.username("ravdes")
								.email("ravdes@gmail.com")
								.password("ravdes123")
								.userRole(UserRole.USER)
								.build();
		userRepository.save(user);

		UserPojo foundUser = userRepository.findById(user.getId()).get();

		assertThat(foundUser).isNotNull();

	}

	@Test
	void shouldUpdateUser() {
		UserPojo user = UserPojo.builder()
								.username("ravdes")
								.email("ravdes@gmail.com")
								.password("ravdes123")
								.userRole(UserRole.USER)
								.build();
		userRepository.save(user);

		UserPojo foundUser = userRepository.findById(user.getId()).get();
		foundUser.setUsername("random123");
		foundUser.setPassword("password321");
		UserPojo updatedUser = userRepository.save(foundUser);

		assertThat(updatedUser.getUsername()).isEqualTo("random123");
		assertThat(updatedUser.getPassword()).isEqualTo("password321");
	}

	@Test
	void shouldDeleteUser() {
		UserPojo user = UserPojo.builder()
								.username("ravdes")
								.email("ravdes@gmail.com")
								.password("ravdes123")
								.userRole(UserRole.USER)
								.build();
		userRepository.save(user);
		userRepository.delete(user);

		Optional<UserPojo> foundUser = userRepository.findById(user.getId());

		assertThat(foundUser).isEmpty();


	}

	@Test
	void shouldFindUserByEmail() {
		UserPojo user = UserPojo.builder()
								.username("ravdes")
								.email("ravdes@gmail.com")
								.password("ravdes123")
								.userRole(UserRole.USER)
								.build();
		userRepository.save(user);

		UserPojo foundUser = userRepository.findByEmail("ravdes@gmail.com").get();

		assertThat(foundUser).isNotNull();
		assertThat(foundUser.getEmail()).isEqualTo("ravdes@gmail.com");

	}

	@Test
	void shouldFindUserByUsername() {
		UserPojo user = UserPojo.builder()
								.username("ravdes")
								.email("ravdes@gmail.com")
								.password("ravdes123")
								.userRole(UserRole.USER)
								.build();
		userRepository.save(user);

		UserPojo foundUser = userRepository.findByUsername("ravdes").get();

		assertThat(foundUser).isNotNull();
		assertThat(foundUser.getUsername()).isEqualTo("ravdes");


	}

	@Test
	void shouldEnableUser() {
		UserPojo user = UserPojo.builder()
								.username("ravdes")
								.email("ravdes@gmail.com")
								.password("ravdes123")
								.userRole(UserRole.USER)
								.build();
		userRepository.save(user);
		UserPojo disabledUser = userRepository.findByUsername("ravdes").get();

		assertThat(disabledUser.getEnabled()).isFalse();

		userRepository.enableUser("ravdes@gmail.com");
		entityManager.clear();
		UserPojo enabledUser = userRepository.findByEmail("ravdes@gmail.com").get();

		assertThat(enabledUser.getEnabled()).isTrue();


	}

	@Test
	void shouldEnableTwoFactorAuthForUser() {
		UserPojo user = UserPojo.builder()
								.username("ravdes")
								.email("ravdes@gmail.com")
								.password("ravdes123")
								.userRole(UserRole.USER)
								.build();
		userRepository.save(user);
		UserPojo disabledUser = userRepository.findByUsername("ravdes").get();

		assertThat(disabledUser.getMfa()).isFalse();

		userRepository.enableTwoFactorAuth("ravdes@gmail.com");
		entityManager.clear();
		UserPojo enabledUser = userRepository.findByEmail("ravdes@gmail.com").get();

		assertThat(enabledUser.getMfa()).isTrue();
	}

	@Test
	void shouldUpdateSecretForUser() {
		UserPojo user = UserPojo.builder()
								.username("ravdes")
								.email("ravdes@gmail.com")
								.password("ravdes123")
								.userRole(UserRole.USER)
								.build();
		userRepository.save(user);
		UserPojo disabledUser = userRepository.findByUsername("ravdes").get();

		assertThat(disabledUser.getSecret()).isEmpty();

		userRepository.updateSecret("secret123", "ravdes@gmail.com");
		entityManager.clear();
		UserPojo enabledUser = userRepository.findByEmail("ravdes@gmail.com").get();

		assertThat(enabledUser.getSecret()).isEqualTo("secret123");
	}

}