package nwt.kts.backend.repository;

import nwt.kts.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findRoleByName(String name);
}
