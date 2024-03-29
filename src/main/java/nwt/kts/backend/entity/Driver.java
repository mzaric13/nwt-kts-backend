package nwt.kts.backend.entity;

import nwt.kts.backend.dto.creation.DriverCreationDTO;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "drivers")
public class Driver extends User {

    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private Point location;

    @Column(name = "time_of_login")
    private Timestamp timeOfLogin;

    @Column(name = "has_future_drive", columnDefinition = "boolean default false")
    private Boolean hasFutureDrive;

    public Driver() {
        this.isBlocked = false;
        this.isAvailable = false;
    }

    public Driver(Integer id, String email, String phoneNumber, String password, String name, String surname, String city,
                  Role role, boolean isBlocked, boolean isAvailable, Vehicle vehicle, Point location) {
        super(id, email, phoneNumber, password, name, surname, city, role);
        this.isBlocked = isBlocked;
        this.isAvailable = isAvailable;
        this.vehicle = vehicle;
        this.location = location;
    }

    /**
     * Constructor used when registering a new Driver into the system.
     *
     * @param driverCreationDTO DriverDTO
     * @param role      Role
     */
    public Driver(String email, String phoneNumber, String password, String name, String surname,
                  String city, DriverCreationDTO driverCreationDTO, Role role, Type type, Provider provider, Point location){
        super(email, phoneNumber, password, name,
                surname, city, role);
        this.provider = provider;
        this.isBlocked = false;
        this.isAvailable = false;
        this.picture = "default.jpg";
        this.vehicle = new Vehicle(driverCreationDTO.getVehicleCreationDTO().getRegistrationNumber(), driverCreationDTO.getVehicleCreationDTO().getName(), type);
        this.location = location;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public Timestamp getTimeOfLogin() {
        return timeOfLogin;
    }

    public void setTimeOfLogin(Timestamp timeOfLogin) {
        this.timeOfLogin = timeOfLogin;
    }

    public boolean isHasFutureDrive() {
        return hasFutureDrive;
    }

    public void setHasFutureDrive(boolean hasFutureDrive) {
        this.hasFutureDrive = hasFutureDrive;
    }
}
