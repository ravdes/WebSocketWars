package ravdes.tictactoe.login;

import org.springframework.web.bind.annotation.*;
import ravdes.tictactoe.jwt.JwtTokenResponse;

@RestController
@RequestMapping(path = "/login")

public class LoginController {
	private LoginService loginService;

	public LoginController(LoginService loginService) {
		this.loginService = loginService;
	}

	@PostMapping
	public JwtTokenResponse login(@RequestBody LoginRequest body) {
		return loginService.validateUser(body);
	}

	@GetMapping(path = "test")
	public String confirm() {
		return "works";
	}


}
