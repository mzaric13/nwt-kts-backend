package nwt.kts.backend.dto.creation;

public class PasswordChangeCreationDTO {

    private String email;
    private String newPassword;
    private String newPasswordConfirmation;

    public PasswordChangeCreationDTO() {

    }

    public PasswordChangeCreationDTO(String email, String newPassword, String newPasswordConfirmation) {
        this.email = email;
        this.newPassword = newPassword;
        this.newPasswordConfirmation = newPasswordConfirmation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordConfirmation() {
        return newPasswordConfirmation;
    }

    public void setNewPasswordConfirmation(String newPasswordConfirmation) {
        this.newPasswordConfirmation = newPasswordConfirmation;
    }
}
