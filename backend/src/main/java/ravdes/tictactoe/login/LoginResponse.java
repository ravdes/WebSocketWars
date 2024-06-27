package ravdes.tictactoe.login;

public record LoginResponse(String bearer_token, boolean mfaRequired) {
}
