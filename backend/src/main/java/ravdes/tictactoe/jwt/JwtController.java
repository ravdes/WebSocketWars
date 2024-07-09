package ravdes.tictactoe.jwt;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ravdes.tictactoe.jwt.dto.BlacklistTokenRequest;

@RestController

public class JwtController {
	private final JwtService jwtService;

	public JwtController(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@PostMapping(path = "/blacklistToken")
	public void blacklistToken(@RequestBody BlacklistTokenRequest request) {
		jwtService.addToBlacklist(request.bearer_token());
	}
}
