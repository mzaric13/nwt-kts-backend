package nwt.kts.backend.dto.creation;

public class ProfilePictureCreationDTO {

    private String email;
    private String profilePicturePath;

    public ProfilePictureCreationDTO(){

    }

    public ProfilePictureCreationDTO(String email, String profilePicturePath) {
        this.email = email;
        this.profilePicturePath = profilePicturePath;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }
}
