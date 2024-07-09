package ravdes.tictactoe.twofactorauth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ravdes.tictactoe.twofactorauth.dto.TwoFactorAuthRequest;
import ravdes.tictactoe.user.UserService;

@RestController

public class TwoFactorAuthController {
	private final UserService userService;

	public TwoFactorAuthController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping(path = "/enable2FA")
	public String enableTwoFactorAuthentication()  {
		return userService.generateQRAndUpdateUser();
	}

	@PostMapping(path = "/verify2FA")
	public String verifyTwoFactorAuthCode(@RequestBody TwoFactorAuthRequest request)  {
		return userService.validate2FA(request.code());
	}




}
