package ravdes.tictactoe.emailsending;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

	@Mock
	private JavaMailSender mailSender;
	@Mock
	private MimeMessage mimeMessage;
	@InjectMocks
	private EmailService emailService;

	@BeforeEach
	void setUp() {
		when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
	}

	@Test
	void shouldSendEmail() throws Exception {
		String to = "ravdes@gmail.com";
		String emailContent = "<h1>Test Email</h1>";
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
		helper.setText(emailContent, true);
		helper.setTo(to);
		helper.setSubject("Confirmed your email");
		helper.setFrom("tictactoe21online@gmail.com");

		emailService.send(to, emailContent);

		verify(mailSender, times(1)).send(mimeMessage);
	}

	@Test
	void shouldThrowError() {
		doThrow(new IllegalStateException("Failed to send email")).when(mailSender).send(any(MimeMessage.class));

		assertThrows(IllegalStateException.class, () -> emailService.send("test@example.com", "Test email body"));
	}
}