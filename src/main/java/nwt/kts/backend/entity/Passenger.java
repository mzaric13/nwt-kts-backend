package nwt.kts.backend.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="passengers")
public class Passenger extends User {

    @Column(name="isBlocked", nullable=false)
    private boolean isBlocked;

    //TODO
    //paymentData - vrv tabela zasebn   a
    //@Column(name="paymentData", nullable=false)
    //private PaymentData paymentData

    //TODO
    //Lista omiljenih ruta fali

    @Column(name = "activated", nullable = false)
    private boolean activated;

    public Passenger(){
        this.isBlocked = false;
    }

    public Passenger(Integer id, String email, String phoneNumber, String password, String name, String surname,
                     String city, Role role, boolean isBlocked, boolean activated /*,PaymentData paymentData*, omiljeneRute*/){
        super(id, email, phoneNumber, password, name, surname, city, role);
        this.isBlocked = isBlocked;
        this.activated = activated;
        //TODO
        //this.paymentData = paymentData;
        //omiljeneRute
    }

    public Passenger(String email, String phoneNumber, String password, String name, String surname,
                     String city, Role role, boolean isBlocked, boolean activated, String profilePicture) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.city = city;
        this.role = role;
        this.isBlocked = isBlocked;
        this.activated = activated;
        this.profilePicture = profilePicture;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}
