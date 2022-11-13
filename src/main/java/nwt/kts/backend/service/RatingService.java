package nwt.kts.backend.service;

import nwt.kts.backend.dto.creation.RatingCreationDTO;
import nwt.kts.backend.entity.Drive;
import nwt.kts.backend.entity.Passenger;
import nwt.kts.backend.entity.Rating;
import nwt.kts.backend.repository.DriveRepository;
import nwt.kts.backend.repository.PassengerRepository;
import nwt.kts.backend.repository.RatingRepository;
import nwt.kts.backend.validation.RatingValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;

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

    public Rating createRating(RatingCreationDTO ratingCreationDTO) {
        Drive drive = driveRepository.findDriveById(ratingCreationDTO.getDriveId());
        Passenger passenger = passengerRepository.findPassengersById(ratingCreationDTO.getPassengerId());
        Rating rating = ratingRepository.findRatingByDriveAndPassenger(drive, passenger);
        if (rating == null) {
            ratingValidator.validateRatingCreation(drive.getEndDate());
            Rating newRating = new Rating(ratingCreationDTO, drive, passenger);
            return ratingRepository.save(newRating);
        } else throw new EntityExistsException(ENTITY_EXISTS_EXCEPTION_MESSAGE);
    }
}
