package nwt.kts.backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import nwt.kts.backend.dto.creation.UpdatedUserDataCreationDTO;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name="driver_data")
public class DriverData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    @Column(name="date_of_request", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Timestamp dateOfRequest;

    @Column(name = "is_answered", nullable = false)
    private boolean isAnswered;

    public DriverData() {
    }

    public DriverData(Integer id, String email, String name, String surname, String city, String phoneNumber, Timestamp dateOfRequest, boolean isAnswered) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.dateOfRequest = dateOfRequest;
        this.isAnswered = isAnswered;
    }

    public DriverData(UpdatedUserDataCreationDTO updatedUserDataCreationDTO){
        this.email = updatedUserDataCreationDTO.getEmail();
        this.name = updatedUserDataCreationDTO.getName();
        this.surname = updatedUserDataCreationDTO.getSurname();
        this.city = updatedUserDataCreationDTO.getCity();
        this.phoneNumber = updatedUserDataCreationDTO.getPhoneNumber();
        this.isAnswered = false;
        this.dateOfRequest = new Timestamp(System.currentTimeMillis());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public void setAnswered(boolean answered) {
        isAnswered = answered;
    }

    public Timestamp getDateOfRequest() {
        return dateOfRequest;
    }

    public void setDateOfRequest(Timestamp dateOfRequest) {
        this.dateOfRequest = dateOfRequest;
    }
}
