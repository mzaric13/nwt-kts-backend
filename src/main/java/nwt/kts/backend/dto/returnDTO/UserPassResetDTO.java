package nwt.kts.backend.dto.returnDTO;

public class UserPassResetDTO {

    private String message;

    private boolean exist;

    public UserPassResetDTO() {
    }

    public UserPassResetDTO(String message, boolean exist) {
        this.message = message;
        this.exist = exist;
    }

    public String getMessage() {
        return message;
    }

    public boolean isExist() {
        return exist;
    }
}
