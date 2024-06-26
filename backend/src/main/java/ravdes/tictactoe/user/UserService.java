package ravdes.tictactoe.user;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ravdes.tictactoe.registration.confirmationtoken.ConfirmationToken;
import ravdes.tictactoe.registration.confirmationtoken.ConfirmationTokenService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor

public class UserService implements UserDetailsService {
	private final static String USER_NOT_FOUND_MSG = "User with email %s not found";
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final ConfirmationTokenService confirmationTokenService;

	 @Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username)
							 .orElseThrow(() -> new UsernameNotFoundException
									 (String.format(USER_NOT_FOUND_MSG, username)));
	}

	public String signUpUser(UserPojo userPojo) {
		 boolean userExists = userRepository.findByEmail(userPojo.getEmail()).isPresent();
		 if(userExists) {
			 throw new IllegalStateException("There's already account registered with this email");
		 }

		 String encodedPassword = bCryptPasswordEncoder.encode(userPojo.getPassword());
		 userPojo.setPassword(encodedPassword);

		 userRepository.save(userPojo);

		 String token = UUID.randomUUID().toString();
		 ConfirmationToken confirmationToken = new ConfirmationToken (
				 token,
				 LocalDateTime.now(),
				 LocalDateTime.now().plusMinutes(15),
				 userPojo);
		 confirmationTokenService.saveConfirmationToken(confirmationToken);

		 return token;
	}

	public int enableUser(String email) {
		return userRepository.enableUser(email);
	}

}
