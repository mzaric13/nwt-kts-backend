package nwt.kts.backend.service;

import nwt.kts.backend.dto.creation.PassengerRatingDTO;
import nwt.kts.backend.dto.creation.RatingCreationDTO;
import nwt.kts.backend.entity.Drive;
import nwt.kts.backend.entity.Passenger;
import nwt.kts.backend.entity.Rating;
import nwt.kts.backend.entity.Status;
import nwt.kts.backend.repository.DriveRepository;
import nwt.kts.backend.repository.PassengerRepository;
import nwt.kts.backend.repository.RatingRepository;
import nwt.kts.backend.validation.RatingValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private DriveRepository driveRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    private final RatingValidator ratingValidator = new RatingValidator();
    private String ENTITY_EXISTS_EXCEPTION_MESSAGE = "Rating has already been given by the passenger.";

    public Rating createRating(RatingCreationDTO ratingCreationDTO, Passenger passenger) {
        Drive drive = driveRepository.findDriveById(ratingCreationDTO.getDriveId());
        ratingCreationDTO.setPassengerId(passenger.getId());
        Rating rating = ratingRepository.findRatingByDriveAndPassenger(drive, passenger);
        if (rating == null) {
            ratingValidator.validateRatingCreation(drive.getEndDate());
            Rating newRating = new Rating(ratingCreationDTO, drive, passenger);
            return ratingRepository.save(newRating);
        } else throw new EntityExistsException(ENTITY_EXISTS_EXCEPTION_MESSAGE);
    }

    public List<Rating> getDriveRatings(Integer id) {
        return ratingRepository.findRatingByDrive_Id(id);
    }

    public List<Double> getDriverAndVehicleAverageRating(Integer id) {
        List<Drive> drives = driveRepository.findAllByDriver_IdAndStatus(id, Status.FINISHED);
        List<Double> avgRatings = new ArrayList<>();
        avgRatings.add(0.0);
        avgRatings.add(0.0);
        double sumOfDriverRatings = 0.0;
        double sumOfVehicleRatings = 0.0;
        double numberOfDriverRatings = 0.0;
        double numberOfVehicleRatings = 0.0;
        if (drives.size() == 0) {
            return avgRatings;
        }
        for (Drive drive:
             drives) {
            List<Rating> ratings = ratingRepository.findRatingByDrive_Id(drive.getId());
            if (ratings.size() == 0) {
                continue;
            }
            for (Rating rating:
                 ratings) {
                if (rating.getDriverRating() > 0) {
                    sumOfDriverRatings += rating.getDriverRating();
                    numberOfDriverRatings++;
                }
                if (rating.getVehicleRating() > 0) {
                    sumOfVehicleRatings += rating.getVehicleRating();
                    numberOfVehicleRatings++;
                }
            }
        }
        avgRatings.set(0, sumOfDriverRatings/numberOfDriverRatings);
        avgRatings.set(1, sumOfVehicleRatings/numberOfVehicleRatings);
        return avgRatings;
    }

    public List<PassengerRatingDTO> findPassengersEligibleRatings(Passenger passenger) {
        List<PassengerRatingDTO> passengerCanRateDrive = new ArrayList<>();
        List<Drive> drives = driveRepository.findAllByPassengersContains(passenger);
        for (Drive drive:
             drives) {
            Rating rating = ratingRepository.findRatingByDriveAndPassenger(drive, passenger);
            if (rating != null) {
                passengerCanRateDrive.add(new PassengerRatingDTO(drive.getId(), false));
            }
            else {
                long today = new Timestamp(System.currentTimeMillis()).getTime();
                long driveDate = drive.getEndDate().getTime();
                long timeDiff = Math.abs(today - driveDate);
                if (TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS) > 3) {
                    passengerCanRateDrive.add(new PassengerRatingDTO(drive.getId(), false));
                }
                else {
                    passengerCanRateDrive.add(new PassengerRatingDTO(drive.getId(), true));
                }
            }
        }
        return passengerCanRateDrive;
    }
}
