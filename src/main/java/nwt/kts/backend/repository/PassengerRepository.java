package nwt.kts.backend.repository;

import nwt.kts.backend.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger, Integer> {

    Passenger findPassengerByEmail(String email);
}
