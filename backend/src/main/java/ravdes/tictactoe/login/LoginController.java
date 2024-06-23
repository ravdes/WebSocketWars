package ravdes.tictactoe.login;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/login")

public class LoginController {
	private LoginService loginService;

	public LoginController(LoginService loginService) {
		this.loginService = loginService;
	}

	@PostMapping
	public boolean login(@RequestBody LoginRequest body) {
		return loginService.validateUser(body);
	}


}
