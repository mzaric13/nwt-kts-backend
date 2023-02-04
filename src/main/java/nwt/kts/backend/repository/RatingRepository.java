package nwt.kts.backend.repository;

import nwt.kts.backend.entity.Drive;
import nwt.kts.backend.entity.Passenger;
import nwt.kts.backend.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Integer> {

    Rating findRatingByDriveAndPassenger(Drive drive, Passenger passenger);

    List<Rating> findRatingByDrive_Id(Integer id);

}
