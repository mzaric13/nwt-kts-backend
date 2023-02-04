package nwt.kts.backend.dto.returnDTO;

public class PasswordResetDTO {

    private String password;

    private String confirmPassword;

    public PasswordResetDTO() {
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
}
