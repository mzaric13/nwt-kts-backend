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
    //paymentData - vrv tabela zasebna
    //@Column(name="paymentData", nullable=false)
    //private PaymentData paymentData

    //TODO
    //Lista omiljenih ruta fali

    public Passenger(){
        this.isBlocked = false;
    }

    public Passenger(Integer id, String email, String phoneNumber, String password, String name, String surname,
                     String city, Role role, boolean isBlocked /*,PaymentData paymentData*, omiljeneRute*/){
        super(id, email, phoneNumber, password, name, surname, city, role);
        this.isBlocked = isBlocked;
        //TODO
        //this.paymentData = paymentData;
        //omiljeneRute
    }
}
