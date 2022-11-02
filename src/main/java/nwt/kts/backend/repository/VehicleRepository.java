package nwt.kts.backend.repository;

import nwt.kts.backend.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

}
