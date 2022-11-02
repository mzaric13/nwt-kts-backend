package nwt.kts.backend.repository;

import nwt.kts.backend.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeRepository extends JpaRepository<Type, Integer> {

    public Type findTypeByName(String name);
}
