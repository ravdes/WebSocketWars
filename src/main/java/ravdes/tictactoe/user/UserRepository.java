package ravdes.tictactoe.user;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
//@Transactional(readOnly = true)

public interface UserRepository {
	Optional<User> findByEmail(String email);
}
