package nwt.kts.backend.exceptions;

public class NotEnoughTokensException extends RuntimeException {

    public NotEnoughTokensException(String errorMessage){
        super(errorMessage);
    }
}
