package nwt.kts.backend.exceptions;

public class NonExistingEntityException extends RuntimeException{

    public NonExistingEntityException(String message) {
        super(message);
    }
}
