package nwt.kts.backend.exceptions;

public class DriverNotFoundException extends RuntimeException {

    public DriverNotFoundException(String errorMessage){
        super(errorMessage);
    }
}
