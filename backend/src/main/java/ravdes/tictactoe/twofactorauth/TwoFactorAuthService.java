package ravdes.tictactoe.twofactorauth;

import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.stereotype.Service;
import static dev.samstevens.totp.util.Utils.getDataUriForImage;
import dev.samstevens.totp.secret.DefaultSecretGenerator;


@Service

public class TwoFactorAuthService {

	public String generateTwoFactorAuthSecret() {
		return new DefaultSecretGenerator().generate();
	}

	public String getQRCode(String email, String secret) throws IllegalStateException {
		QrData data = new QrData.Builder()
				.label(email)
				.secret(secret)
				.issuer("TicTacToe Spring")
				.algorithm(HashingAlgorithm.SHA1)
				.digits(6)
				.period(30)
				.build();


		QrGenerator generator = new ZxingPngQrGenerator();
		byte[] imageData = new byte[0];

		try {
			imageData = generator.generate(data);
		} catch (QrGenerationException e) {
			throw new IllegalStateException("Error generating QRCode");
		}


		return getDataUriForImage(imageData, generator.getImageMimeType());
	}

	public boolean isOtpValid(String secret, String code) {
		TimeProvider timeProvider = new SystemTimeProvider();
		CodeGenerator codeGenerator = new DefaultCodeGenerator();
		CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
		return verifier.isValidCode(secret, code);
	}

}
