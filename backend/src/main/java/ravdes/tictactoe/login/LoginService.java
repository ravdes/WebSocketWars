package ravdes.tictactoe.login;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ravdes.tictactoe.user.UserPojo;
import ravdes.tictactoe.user.UserRepository;
import java.util.Optional;

@Service

public class LoginService {
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public LoginService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	public boolean validateUser(LoginRequest request) {
		Optional<UserPojo> userOptional = userRepository.findByEmail(request.email());

		if (userOptional.isPresent()) {
			UserPojo user = userOptional.get();
			boolean isEnabled = user.getEnabled();
			String hashedPassword = user.getPassword();
			boolean passwordMatch = bCryptPasswordEncoder.matches(request.password(), hashedPassword);

			if(!isEnabled) {
				throw new IllegalStateException("User is not enabled!");
			} else if(!passwordMatch) {
				throw new IllegalStateException("Incorrect password!");
			}
			return true;

		} else {
			throw new IllegalStateException("Account doesn't exist");
		}
	}
}