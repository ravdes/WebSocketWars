package ravdes.tictactoe.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)

public interface UserRepository extends JpaRepository<UserPojo, Long> {
	Optional<UserPojo> findByEmail(String email);
	Optional<UserPojo> findByUsername(String username);

	@Transactional
	@Modifying
	@Query("update UserPojo u set u.enabled = true where u.email = ?1")
	int enableUser(String email);
}
