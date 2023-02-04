package nwt.kts.backend.dto.returnDTO;

import nwt.kts.backend.entity.User;

public class UserDTO {

    private String email;

    private String phoneNumber;

    private String name;

    private String surname;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.name = user.getName();
        this.surname = user.getSurname();
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
}
