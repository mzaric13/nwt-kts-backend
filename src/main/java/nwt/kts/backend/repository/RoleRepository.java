package nwt.kts.backend.repository;

import nwt.kts.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    public Role findRoleByName(String name);
}
