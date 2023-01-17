package nwt.kts.backend.dto.returnDTO;

import nwt.kts.backend.entity.DriverData;

import java.sql.Timestamp;

public class DriverDataDTO {

    private Integer id;
    private String email;
    private String name;
    private String surname;
    private String city;
    private String phoneNumber;
    private Timestamp dateOfRequest;
    private boolean isAnswered;

    public DriverDataDTO() {

    }

    public DriverDataDTO(Integer id, String email, String name, String surname, String city, String phoneNumber, Timestamp dateOfRequest, boolean isAnswered) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.dateOfRequest = dateOfRequest;
        this.isAnswered = isAnswered;
    }

    public DriverDataDTO(DriverData driverData) {
        this.id = driverData.getId();
        this.email = driverData.getEmail();
        this.name = driverData.getName();
        this.surname = driverData.getSurname();
        this.city = driverData.getCity();
        this.phoneNumber = driverData.getPhoneNumber();
        this.dateOfRequest = driverData.getDateOfRequest();
        this.isAnswered = driverData.isAnswered();
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isAnswered() {
        return isAnswered;
    }

    public void setIsAnswered(boolean answered) {
        isAnswered = answered;
    }

    public Timestamp getDateOfRequest() {
        return dateOfRequest;
    }

    public void setDateOfRequest(Timestamp dateOfRequest) {
        this.dateOfRequest = dateOfRequest;
    }
}
