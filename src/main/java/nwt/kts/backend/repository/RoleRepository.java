package nwt.kts.backend.repository;

import nwt.kts.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface RoleRepository extends JpaRepository<Role, Integer> {

    @Query("select r from Role r where r.name = ?1")
    public Role findRoleByName(String name);
}
