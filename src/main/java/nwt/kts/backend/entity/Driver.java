package nwt.kts.backend.entity;

import nwt.kts.backend.dto.creation.DriverCreationDTO;

import javax.persistence.*;

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

    public Driver() {
        this.isBlocked = false;
        this.isAvailable = false;
    }

    public Driver(Integer id, String email, String phoneNumber, String password, String name, String surname, String city, Role role, boolean isBlocked, boolean isAvailable, Vehicle vehicle) {
        super(id, email, phoneNumber, password, name, surname, city, role);
        this.isBlocked = isBlocked;
        this.isAvailable = isAvailable;
        this.vehicle = vehicle;
    }

    /**
     * Constructor used when registering a new Driver into the system.
     *
     * @param driverCreationDTO DriverDTO
     * @param role      Role
     */
    public Driver(DriverCreationDTO driverCreationDTO, Role role, Type type){
        super(driverCreationDTO.getEmail(), driverCreationDTO.getPhoneNumber(), driverCreationDTO.getPassword(), driverCreationDTO.getName(),
                driverCreationDTO.getSurname(), driverCreationDTO.getCity(), role);
        this.isBlocked = false;
        this.isAvailable = false;
        this.profilePicture = "default.jpg";
        this.vehicle = new Vehicle(driverCreationDTO.getVehicleCreationDTO().getRegistrationNumber(), driverCreationDTO.getVehicleCreationDTO().getName(), type);
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
}
