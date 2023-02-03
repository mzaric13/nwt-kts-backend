package nwt.kts.backend.dto.returnDTO;
import nwt.kts.backend.entity.Driver;

public class DriverDTO {

    private Integer id;
    private String email;
    private String phoneNumber;
    private String password;
    private String name;
    private String surname;
    private String city;
    private VehicleReturnDTO vehicle;
    private String profilePicture;
    private boolean blocked;
    private boolean available;

    private PointDTO location;
    private ImageDataDTO imageData;

    public DriverDTO() {

    }

    public DriverDTO(Integer id, String email, String phoneNumber, String password, String name,
                     String surname, String city, String profilePicture, VehicleReturnDTO vehicle, PointDTO location) {
        this.id = id;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.city = city;
        this.profilePicture = profilePicture;
        this.vehicle = vehicle;
        this.blocked = false;
        this.available = false;
        this.location = location;
    }

    public DriverDTO(Driver driver){
        this.id = driver.getId();
        this.email = driver.getEmail();
        this.phoneNumber = driver.getPhoneNumber();
        this.password = driver.getPassword();
        this.name = driver.getName();
        this.surname = driver.getSurname();
        this.city = driver.getCity();
        this.profilePicture = driver.getPicture();
        this.blocked = driver.isBlocked();
        this.available = driver.isAvailable();
        this.vehicle = new VehicleReturnDTO(driver.getVehicle());
        this.location = new PointDTO(driver.getLocation());
    }

    public DriverDTO(Driver driver, ImageDataDTO imageDataDTO) {
        this.id = driver.getId();
        this.email = driver.getEmail();
        this.phoneNumber = driver.getPhoneNumber();
        this.password = driver.getPassword();
        this.name = driver.getName();
        this.surname = driver.getSurname();
        this.city = driver.getCity();
        this.profilePicture = driver.getPicture();
        this.blocked = driver.isBlocked();
        this.available = driver.isAvailable();
        this.vehicle = new VehicleReturnDTO(driver.getVehicle());
        this.location = new PointDTO(driver.getLocation());
        this.imageData = imageDataDTO;
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
        return blocked;
    }

    public void setIsBlocked(boolean blocked) {
        blocked = blocked;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setIsAvailable(boolean available) {
        available = available;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public PointDTO getLocation() {
        return location;
    }

    public void setLocation(PointDTO location) {
        this.location = location;
    }

    public ImageDataDTO getImageData() {
        return imageData;
    }
}
