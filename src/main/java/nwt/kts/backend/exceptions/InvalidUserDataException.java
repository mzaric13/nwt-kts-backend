package nwt.kts.backend.exceptions;

public class InvalidUserDataException extends RuntimeException{
    public InvalidUserDataException(String errorMessage){
        super(errorMessage);
    }
}
