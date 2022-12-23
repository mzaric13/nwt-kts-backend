package nwt.kts.backend.dto.returnDTO;

import nwt.kts.backend.entity.Passenger;

import javax.persistence.Column;
import java.util.Set;
import java.util.stream.Collectors;

public class PassengerDTO {

    private int id;

    private String email;

    private String phoneNumber;

    private String name;

    private String surname;

    private String city;

    private String profilePicture;

    private boolean isBlocked;

    private boolean activated;

    private Set<RouteDTO> favoriteRoutes;

    private int tokens;

    public PassengerDTO() {
    }

    public PassengerDTO(Passenger passenger) {
        this.id = passenger.getId();
        this.email = passenger.getEmail();
        this.phoneNumber = passenger.getPhoneNumber();
        this.name = passenger.getName();
        this.surname = passenger.getSurname();
        this.city = passenger.getCity();
        this.profilePicture = passenger.getPicture();
        this.activated = passenger.isActivated();
        this.isBlocked = passenger.isBlocked();
        this.favoriteRoutes = passenger.getFavouriteRoutes().stream().map(RouteDTO::new).collect(Collectors.toSet());
        this.tokens = passenger.getTokens();
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
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

    public String getProfilePicture() { return profilePicture; }

    public boolean isActivated() {
        return activated;
    }

    public boolean isBlocked() {return isBlocked; }

    public Set<RouteDTO> getFavoriteRoutes() {
        return favoriteRoutes;
    }

    public void setFavoriteRoutes(Set<RouteDTO> favoriteRoutes) {
        this.favoriteRoutes = favoriteRoutes;
    }

    public int getTokens() {
        return tokens;
    }
}
