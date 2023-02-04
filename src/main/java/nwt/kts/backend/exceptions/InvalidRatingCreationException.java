package nwt.kts.backend.exceptions;

public class InvalidRatingCreationException extends RuntimeException {
    public InvalidRatingCreationException(String errorMessage){
        super(errorMessage);
    }
}
