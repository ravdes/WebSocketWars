package ravdes.tictactoe.registration;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ravdes.tictactoe.emailsending.EmailSender;
import ravdes.tictactoe.jwt.JwtService;
import ravdes.tictactoe.registration.confirmationtoken.ConfirmationToken;
import ravdes.tictactoe.registration.confirmationtoken.ConfirmationTokenService;
import ravdes.tictactoe.registration.dto.RegistrationRequest;
import ravdes.tictactoe.user.UserPojo;
import ravdes.tictactoe.user.UserRepository;
import ravdes.tictactoe.user.UserRole;
import ravdes.tictactoe.user.UserService;
import ravdes.tictactoe.user.dto.GuestRegistrationRequest;
import ravdes.tictactoe.user.dto.GuestRegistrationResponse;

import java.time.LocalDateTime;
import java.util.Optional;

@Service

public class RegistrationService {
	private final EmailVerifier emailVerifier;
	private final UserService userService;
	private final ConfirmationTokenService confirmationTokenService;
	private final EmailSender emailSender;
	private final JwtService jwtService;
	private final UserRepository userRepository;

	public RegistrationService(EmailVerifier emailVerifier, UserService userService, ConfirmationTokenService confirmationTokenService, EmailSender emailSender, JwtService jwtService, UserRepository userRepository) {
		this.emailVerifier = emailVerifier;
		this.userService = userService;
		this.confirmationTokenService = confirmationTokenService;
		this.emailSender = emailSender;
		this.jwtService = jwtService;
		this.userRepository = userRepository;
	}

	public String register(RegistrationRequest request) {
		boolean isValidEmail = emailVerifier.
				validateEmail(request.email());

		if (!isValidEmail) {
			throw new IllegalStateException("email not valid");
		}

		String token = userService.signUpUser(
				UserPojo.builder()
						.username(request.username())
						.email(request.email())
						.password(request.password())
						.userRole(UserRole.USER)
						.build()


		);

		String link = "http://localhost:8080/registration/confirm?token=" + token;

		emailSender.send(request.email(), buildEmail(request.username(), link));


		return "Successfull, check your email to verify account!";

	}

	public GuestRegistrationResponse registerGuest(GuestRegistrationRequest request) {
		UserPojo user = UserPojo.builder()
								.username(request.username())
								.email(null)
								.password(null)
								.userRole(UserRole.GUEST)
								.build();

		userService.signUpGuest(user);
		String jwtToken = jwtService.generateToken(user);
		return new GuestRegistrationResponse(user.getUsername(), jwtToken);
	}

	public void deleteGuest(GuestRegistrationResponse request) {

		Optional<UserPojo> userToDelete = userRepository.findByUsername(request.nickname());

		if (userToDelete.isPresent()) {
			userRepository.delete(userToDelete.get());
			jwtService.addToBlacklist(request.bearer_token());
		} else {
			throw new IllegalStateException("Guest account with this nickname doesn't exist!");


		}
	}


	@Transactional
	public String confirmToken(String token) {
		ConfirmationToken confirmationToken = confirmationTokenService
				.getToken(token)
				.orElseThrow(() ->
						new IllegalStateException("token not found"));

		if (confirmationToken.getConfirmedAt() != null) {
			throw new IllegalStateException("email already confirmed");
		}

		LocalDateTime expiredAt = confirmationToken.getExpiresAt();

		if (expiredAt.isBefore(LocalDateTime.now())) {
			throw new IllegalStateException("token expired");
		}

		confirmationTokenService.setConfirmedAt(token);

		userService.enableUser(
				confirmationToken.getUserPojo().getEmail());
		return "confirmed";
	}

	private String buildEmail(String name, String link) {
		return "<!DOCTYPE html>\n" +
				"<html lang=\"en\">\n" +
				"<head>\n" +
				"  <meta charset=\"UTF-8\">\n" +
				"  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
				"  <title>Email Template</title>\n" +
				"</head>\n" +
				"<body style=\"font-family:Helvetica, Arial, sans-serif; font-size:16px; margin:0; color:#0b0c0c; background:#f8f9fa;\">\n" +
				"  <div style=\"max-width:580px; margin:0 auto; background:#ffffff; border-radius:8px; box-shadow:0 0 10px rgba(0,0,0,0.1); padding:20px;\">\n" +
				"    <div style=\"padding:10px 0; background:#0b0c0c; text-align:center; border-top-left-radius:8px; border-top-right-radius:8px;\">\n" +
				"      <h1 style=\"font-size:28px; font-weight:700; color:#ffffff; margin:0;\">Confirm your email</h1>\n" +
				"    </div>\n" +
				"    <div style=\"padding:20px; border-top:5px solid #1D70B8;\">\n" +
				"      <p style=\"margin:0 0 20px 0; font-size:19px; line-height:25px; color:#0b0c0c;\">Hi " + name + ",</p>\n" +
				"      <p style=\"margin:0 0 20px 0; font-size:19px; line-height:25px; color:#0b0c0c;\">Thank you for registering. Please click on the below link to activate your account:</p>\n" +
				"      <blockquote style=\"margin:0 0 20px 0; border-left:5px solid #b1b4b6; padding:10px 0 0.1px 15px; font-size:19px; line-height:25px;\">\n" +
				"        <p style=\"margin:0; font-size:19px; line-height:25px; color:#0b0c0c;\"><a href=\"" + link + "\" style=\"color:#1D70B8; text-decoration:none; font-weight:bold;\">Activate Now</a></p>\n" +
				"      </blockquote>\n" +
				"      <p style=\"margin:0 0 20px 0; font-size:19px; line-height:25px; color:#0b0c0c;\">Link will expire in 15 minutes. See you soon!</p>\n" +
				"    </div>\n" +
				"  </div>\n" +
				"</body>\n" +
				"</html>";
	}


}
