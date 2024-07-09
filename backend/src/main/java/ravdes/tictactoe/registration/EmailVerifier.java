package ravdes.tictactoe.registration;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

@Component

public class EmailVerifier {
	private final EmailValidator emailValidator;

	public EmailVerifier() {
		this.emailValidator = EmailValidator.getInstance();
	}

	public boolean validateEmail(String email) {
		return emailValidator.isValid(email);
	}
}