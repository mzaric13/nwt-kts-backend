package nwt.kts.backend.validation;

import nwt.kts.backend.exceptions.InvalidRatingCreationException;

import java.sql.Timestamp;
import java.util.Calendar;

public class RatingValidator {

    private final String EXCEPTION_MESSAGE = "Rating can be created up to three days after the drive has been done.";

    public void validateRatingCreation(Timestamp driveEndTime) {

        Timestamp today = new Timestamp(System.currentTimeMillis());
        Timestamp afterThreeDaysEndTime = addThreeDays(driveEndTime);
        if (today.compareTo(afterThreeDaysEndTime) > 0) {
            throw new InvalidRatingCreationException(EXCEPTION_MESSAGE);
        }
    }

    private Timestamp addThreeDays(Timestamp driveEndTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(driveEndTime);
        calendar.add(Calendar.DAY_OF_WEEK, 3);
        driveEndTime.setTime(calendar.getTime().getTime());
        return driveEndTime;
    }

}
