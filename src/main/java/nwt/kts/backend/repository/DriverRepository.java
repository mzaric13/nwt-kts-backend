package nwt.kts.backend.repository;

import nwt.kts.backend.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Integer> {

    Driver findDriverByEmail(String email);

    List<Driver> findDriversByIsBlocked(boolean blocked);
}
