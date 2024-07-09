package ravdes.tictactoe.login;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ravdes.tictactoe.login.dto.LoginRequest;
import ravdes.tictactoe.login.dto.LoginResponse;

@RestController
@RequestMapping(path = "/login")

public class LoginController {
	private final LoginService loginService;

	public LoginController(LoginService loginService) {
		this.loginService = loginService;
	}

	@PostMapping
	public LoginResponse login(@RequestBody LoginRequest body) {
		return loginService.validateUser(body);
	}


}
