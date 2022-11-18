package nwt.kts.backend.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="passengers")
public class Passenger extends User {

    @Column(name="is_blocked", nullable=false)
    private boolean isBlocked;

    //TODO
    //paymentData - vrv tabela zasebn   a
    //@Column(name="paymentData", nullable=false)
    //private PaymentData paymentData

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "favourite_routes", joinColumns = @JoinColumn(name = "passenger_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "route_id", referencedColumnName = "id"))
    private Set<Route> favouriteRoutes;

    @Column(name = "activated", nullable = false)
    private boolean activated;

    public Passenger(){
        this.isBlocked = false;
        this.favouriteRoutes = new HashSet<>();
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

    public Set<Route> getFavouriteRoutes() {
        return favouriteRoutes;
    }

    public void setFavouriteRoutes(Set<Route> favouriteRoutes) {
        this.favouriteRoutes = favouriteRoutes;
    }

    public void addFavouriteRoute(Route route) {
        this.favouriteRoutes.add(route);
    }
}
