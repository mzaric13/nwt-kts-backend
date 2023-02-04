package nwt.kts.backend.dto.returnDTO;

public class DriverDataAnsweredDTO {
    private boolean isAnswered;

    public DriverDataAnsweredDTO(boolean isAnswered){
        this.isAnswered = isAnswered;
    }

    public boolean isAnswered() {
        return isAnswered;
    }

}
