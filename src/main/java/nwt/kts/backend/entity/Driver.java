package nwt.kts.backend.entity;

import javax.persistence.*;

@Entity
@Table(name = "drivers")
public class Driver extends User {

    @Column(name = "isBlocked", nullable = false)
    private boolean isBlocked;

    @Column(name = "isAvailable", nullable = false)
    private boolean isAvailable;

    @OneToOne(fetch = FetchType.LAZY)
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
}
