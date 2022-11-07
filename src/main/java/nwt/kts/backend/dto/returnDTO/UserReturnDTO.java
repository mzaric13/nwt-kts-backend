package nwt.kts.backend.dto.returnDTO;

import nwt.kts.backend.entity.User;

public class UserReturnDTO {

    private Integer id;
    private String email;
    private String phoneNumber;
    private String password;
    private String name;
    private String surname;
    private String city;

    public UserReturnDTO() {

    }

    public UserReturnDTO(Integer id, String email, String phoneNumber, String password, String name, String surname, String city) {
        this.id = id;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.city = city;
    }

    public UserReturnDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.password = user.getPassword();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.city = user.getCity();
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
}
