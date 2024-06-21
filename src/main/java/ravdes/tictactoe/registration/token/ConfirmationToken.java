package ravdes.tictactoe.registration.token;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ravdes.tictactoe.user.UserPojo;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity

public class ConfirmationToken {

	@SequenceGenerator(
			name = "confirmation_token_sequence",
			sequenceName = "confirmation_token_sequence",
			allocationSize = 1
	)
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "confirmation_token_sequence"
	)

	@Id
	private Long id;

	@Column(nullable = false)
	private String token;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime expiresAt;

	private LocalDateTime confirmedAt;

	@ManyToOne
	@JoinColumn(
			nullable = false,
			name = "user_id"
	)
	private UserPojo userPojo;

	public ConfirmationToken(String token,  LocalDateTime createdAt,LocalDateTime expiresAt, UserPojo userPojo) {
		this.token = token;
		this.createdAt = createdAt;
		this.expiresAt = expiresAt;
		this.userPojo = userPojo;
	}
}
