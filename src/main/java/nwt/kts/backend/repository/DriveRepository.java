package nwt.kts.backend.repository;

import nwt.kts.backend.entity.Drive;
import nwt.kts.backend.entity.Driver;
import nwt.kts.backend.entity.Passenger;
import nwt.kts.backend.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

public interface DriveRepository extends JpaRepository<Drive, Integer> {

    Page<Drive> findAllByStatusOrderByStartDateAsc(Status status, Pageable pageable);

    Page<Drive> findAllByDriverAndStatusOrderByStartDateAsc(Driver driver, Status status, Pageable pageable);

    Page<Drive> findAllByPassengersContainsAndStatusOrderByStartDateAsc(Passenger passenger, Status status, Pageable pageable);

    Drive findDriveById(Integer id);

    List<Drive> findAllByDriver_IdAndStatus(Integer id, Status status);

    List<Drive> findAllByStartDateAfterAndEndDateBeforeAndPassengersContainsOrderByStartDateAsc(Timestamp startDate, Timestamp endDate, Passenger passenger);

    List<Drive> findAllByStartDateAfterAndEndDateBeforeAndDriverOrderByStartDateAsc(Timestamp startDate, Timestamp endDate, Driver driver);

    List<Drive> findAllByStartDateAfterAndEndDateBeforeOrderByStartDateAsc(Timestamp startDate, Timestamp endDate);
}
