package nwt.kts.backend.exceptions;

public class InvalidStartTimeException extends RuntimeException {
    public InvalidStartTimeException(String errorMessage){
        super(errorMessage);
    }
}
