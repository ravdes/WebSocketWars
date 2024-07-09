package ravdes.tictactoe.login.dto;

public record LoginResponse(String username, String bearer_token, boolean mfaRequired) {
}
