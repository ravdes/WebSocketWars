package ravdes.tictactoe.registration;

import org.springframework.web.bind.annotation.*;
import ravdes.tictactoe.registration.dto.RegistrationRequest;
import ravdes.tictactoe.user.dto.GuestRegistrationRequest;
import ravdes.tictactoe.user.dto.GuestRegistrationResponse;

@RestController
@RequestMapping(path = "/registration")

public class RegistrationController {
	private final RegistrationService registrationService;

	public RegistrationController(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}

	@PostMapping
	public String register(@RequestBody RegistrationRequest request) {
		return registrationService.register(request);
	}

	@GetMapping(path = "confirm")
	public String confirm(@RequestParam("token") String token) {
		return registrationService.confirmToken(token);
	}

	@PostMapping(path = "registerGuest")
	public GuestRegistrationResponse registerGuest(@RequestBody GuestRegistrationRequest request) {
		return registrationService.registerGuest(request);
	}

	@PostMapping(path = "deleteGuest")
	public void deleteGuestAccount(@RequestBody GuestRegistrationResponse request) {
		registrationService.deleteGuest(request);
	}
}
