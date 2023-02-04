package nwt.kts.backend.exceptions;

public class UserNotAuthenticatedException extends RuntimeException{

    public UserNotAuthenticatedException(String message) {
        super(message);
    }
}
