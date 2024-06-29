package ravdes.tictactoe.login;

import org.springframework.web.bind.annotation.*;

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
