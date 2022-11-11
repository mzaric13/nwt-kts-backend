package nwt.kts.backend.repository;

import nwt.kts.backend.entity.Drive;
import nwt.kts.backend.entity.Driver;
import nwt.kts.backend.entity.Passenger;
import nwt.kts.backend.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DriveRepository extends JpaRepository<Drive, Integer> {

    Page<Drive> findAllByStatus(Status status, Pageable pageable);

    Page<Drive> findAllByDriverAndStatus(Driver driver, Status status, Pageable pageable);

    Page<Drive> findAllByPassengersContainsAndStatus(Passenger passenger, Status status, Pageable pageable);
}
