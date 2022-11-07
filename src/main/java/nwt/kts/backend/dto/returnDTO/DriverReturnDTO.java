package nwt.kts.backend.dto.returnDTO;
import nwt.kts.backend.entity.Driver;

public class DriverReturnDTO {

    private Integer id;
    private String email;
    private String phoneNumber;
    private String password;
    private String name;
    private String surname;
    private String city;
    private VehicleReturnDTO vehicle;
    private String profilePicture;
    private boolean isBlocked;
    private boolean isAvailable;

    public DriverReturnDTO() {

    }

    public DriverReturnDTO(Integer id, String email, String phoneNumber, String password, String name,
                           String surname, String city, String profilePicture, VehicleReturnDTO vehicle) {
        this.id = id;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.city = city;
        this.profilePicture = profilePicture;
        this.vehicle = vehicle;
        this.isBlocked = false;
        this.isAvailable = false;
    }

    public DriverReturnDTO(Driver driver){
        this.id = driver.getId();
        this.email = driver.getEmail();
        this.phoneNumber = driver.getPhoneNumber();
        this.password = driver.getPassword();
        this.name = driver.getName();
        this.surname = driver.getSurname();
        this.city = driver.getCity();
        this.profilePicture = driver.getProfilePicture();
        this.vehicle = new VehicleReturnDTO(driver.getVehicle());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public VehicleReturnDTO getVehicle() {
        return vehicle;
    }
    
    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setVehicle(VehicleReturnDTO vehicle) {
        this.vehicle = vehicle;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean available) {
        isAvailable = available;
    }
}
