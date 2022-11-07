package nwt.kts.backend.dto.creation;

public class UpdatedUserDataCreationDTO {

    private String email;
    private String name;
    private String surname;
    private String city;
    private String phoneNumber;

    public UpdatedUserDataCreationDTO() {

    }

    public UpdatedUserDataCreationDTO(String email, String name, String surname, String city, String phoneNumber) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.city = city;
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
