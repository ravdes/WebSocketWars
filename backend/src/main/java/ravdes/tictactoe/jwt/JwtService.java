package ravdes.tictactoe.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ravdes.tictactoe.user.UserPojo;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashSet;
import java.util.function.Function;

@Service

public class JwtService {
	private final String SECRET_KEY = "d7301c9a24aa4a1bcf9bf7a55dbfa851a585ad1aeadd355e2c798c277589a76f";
	private final HashSet<String> blacklistedTokens = new HashSet<>();


	private boolean isBlacklisted(String token) {
		return blacklistedTokens.contains(token);
	}

	public void addToBlacklist(String token) {
		blacklistedTokens.add(token);

	}

	public String extractUsername(String token) {
		if (isBlacklisted(token)) {
			throw new IllegalStateException("Logged out, this bearer is not valid anymore");
		}
		return extractClaim(token, Claims::getSubject);
	}

	public boolean isValid(String token, UserDetails user) {
		if (isBlacklisted(token)) {
			return false;
		}
		String username = extractUsername(token);
		return (username.equals(user.getUsername())) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> resolver) {
		Claims claims = extractAllClaims(token);
		return resolver.apply(claims);

	}

	private Claims extractAllClaims(String token) {
		return Jwts
				.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	public String generateToken(UserPojo user) {
		return Jwts
				.builder()
				.subject(user.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + 24*60*60*1000))
				.signWith(getSigningKey())
				.compact();
	}

	private SecretKey getSigningKey() {
		byte [] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(keyBytes);
	}


}
