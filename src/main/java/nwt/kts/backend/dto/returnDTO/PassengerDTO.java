package nwt.kts.backend.dto.returnDTO;

import nwt.kts.backend.entity.Passenger;

import javax.persistence.Column;

public class PassengerDTO {

    private int id;

    private String email;

    private String phoneNumber;

    private String password;

    private String name;

    private String surname;

    private String city;

    private String profilePicture;

    private boolean activated;

    public PassengerDTO() {
    }

    public PassengerDTO(Passenger passenger) {
        this.id = passenger.getId();
        this.email = passenger.getEmail();
        this.phoneNumber = passenger.getPhoneNumber();
        this.password = passenger.getPassword();
        this.name = passenger.getName();
        this.surname = passenger.getSurname();
        this.city = passenger.getCity();
        this.profilePicture = passenger.getProfilePicture();
        this.activated = passenger.isActivated();
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

    public String getPassword() {
        return password;
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
}
