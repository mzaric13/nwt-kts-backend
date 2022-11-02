package nwt.kts.backend.repository;

import nwt.kts.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    public User findUserByEmail(String email);
}
