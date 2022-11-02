package nwt.kts.backend.dto.creation;

public class DriverCreationDTO {

    private String email;
    private String phoneNumber;
    private String password;
    private String passwordConfirmation;
    private String name;
    private String surname;
    private String city;
    private VehicleCreationDTO vehicleCreationDTO;

    public DriverCreationDTO(){

    }

    public DriverCreationDTO(String email, String phoneNumber, String password, String passwordConfirmation, String name, String surname, String city, VehicleCreationDTO vehicleCreationDTO) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
        this.name = name;
        this.surname = surname;
        this.city = city;
        this.vehicleCreationDTO = vehicleCreationDTO;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
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

    public VehicleCreationDTO getVehicleCreationDTO() {
        return vehicleCreationDTO;
    }

    public void setVehicleCreationDTO(VehicleCreationDTO vehicleCreationDTO) {
        this.vehicleCreationDTO = vehicleCreationDTO;
    }
}
