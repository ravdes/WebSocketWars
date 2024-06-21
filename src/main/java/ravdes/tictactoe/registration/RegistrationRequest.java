package ravdes.tictactoe.registration;

public record RegistrationRequest(
		String username,
		String email,
		String password) {
}
