package nwt.kts.backend.exceptions;

public class InvalidVehicleDataException extends RuntimeException{
    public InvalidVehicleDataException(String errorMessage){
        super(errorMessage);
    }
}
