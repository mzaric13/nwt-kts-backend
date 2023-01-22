package nwt.kts.backend.repository;

import nwt.kts.backend.entity.Passenger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface PassengerRepository extends JpaRepository<Passenger, Integer> {

    Passenger findPassengerByEmail(String email);

    List<Passenger> findPassengersByIsBlocked(boolean blocked);

    Passenger findPassengerById(Integer id);

    List<Passenger> findAllByActivatedTrue();

    Page<Passenger> findAllByActivatedTrue(Pageable pageable);
}
