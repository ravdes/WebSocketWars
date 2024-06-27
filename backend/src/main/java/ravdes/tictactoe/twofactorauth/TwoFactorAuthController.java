package ravdes.tictactoe.twofactorauth;

import dev.samstevens.totp.exceptions.QrGenerationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ravdes.tictactoe.user.UserPojo;
import ravdes.tictactoe.user.UserRepository;

import java.util.Optional;

@RestController

public class TwoFactorAuthController {
	private final TwoFactorAuthService twoFactorAuthService;
	private final UserRepository userRepository;


	public TwoFactorAuthController(TwoFactorAuthService twoFactorAuthService, UserRepository userRepository) {
		this.twoFactorAuthService = twoFactorAuthService;
		this.userRepository = userRepository;
	}

	@GetMapping(path = "/enable2FA")
	public String enable2FA()  {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		Optional<UserPojo> userOptional = userRepository.findByUsername(username);

		if (userOptional.isPresent()) {
			UserPojo user = userOptional.get();

			String secret = twoFactorAuthService.generateTwoFactorAuthSecret();
			String email = user.getEmail();
			userRepository.enableTwoFactorAuth(email);
			userRepository.updateSecret(secret, email);

			return twoFactorAuthService.getQRCode(email, secret);

		}

		return "nic";
	}




}
