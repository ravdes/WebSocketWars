package ravdes.tictactoe.user;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ravdes.tictactoe.registration.confirmationtoken.ConfirmationToken;
import ravdes.tictactoe.registration.confirmationtoken.ConfirmationTokenService;
import ravdes.tictactoe.twofactorauth.TwoFactorAuthService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service

public class UserService implements UserDetailsService {
	private final static String USER_NOT_FOUND_MSG = "User with email %s not found";
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final ConfirmationTokenService confirmationTokenService;
	private final TwoFactorAuthService twoFactorAuthService;

	public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, ConfirmationTokenService confirmationTokenService, TwoFactorAuthService twoFactorAuthService) {
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.confirmationTokenService = confirmationTokenService;
		this.twoFactorAuthService = twoFactorAuthService;
	}

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

	public void enableUser(String email) {
		userRepository.enableUser(email);
	}

	public String generateQRAndUpdateUser() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		Optional<UserPojo> userOptional = userRepository.findByUsername(username);

		if (userOptional.isPresent()) {
			UserPojo user = userOptional.get();

			String secret = twoFactorAuthService.generateTwoFactorAuthSecret();
			String email = user.getEmail();
			userRepository.enableTwoFactorAuth(email);
			userRepository.updateSecret(secret, email);

			return twoFactorAuthService.getQRCode(email, secret);

		} else {
			throw new IllegalStateException("User doesn't exist!");

		}


	}

	public String validate2FA(String code) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		Optional<UserPojo> userOptional = userRepository.findByUsername(username);

		if (userOptional.isPresent()) {
			UserPojo user = userOptional.get();
			String userSecret = user.getSecret();

			if (twoFactorAuthService.isOtpValid(userSecret, code)) {
				return "Inserted code correct!";
			} else {
				throw new IllegalStateException("Incorrect code!");
			}


		} else {
			throw new IllegalStateException("User not found!");

		}
	}



}
