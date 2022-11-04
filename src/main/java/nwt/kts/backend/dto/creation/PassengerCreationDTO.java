package nwt.kts.backend.dto.creation;

public class PassengerCreationDTO {

    private String email;

    private String phoneNumber;

    private String password;

    private String passwordConfirm;

    private String name;

    private String surname;

    private String city;

    public PassengerCreationDTO() {
    }

    public PassengerCreationDTO(String email, String phoneNumber, String password, String passwordConfirm, String name, String surname, String city) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.name = name;
        this.surname = surname;
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getCity() {
        return city;
    }
}
