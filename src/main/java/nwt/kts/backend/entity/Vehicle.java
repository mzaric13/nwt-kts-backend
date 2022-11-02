package nwt.kts.backend.entity;

import javax.persistence.*;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "registrationNumber", unique = true, nullable = false)
    private String registrationNumber;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private Type type;

    public Vehicle() {

    }

    public Vehicle(Integer id, String registrationNumber, String name, Type type) {
        this.id = id;
        this.registrationNumber = registrationNumber;
        this.name = name;
        this.type = type;
    }

    public Vehicle(String registrationNumber, String name, Type type) {
        this.registrationNumber = registrationNumber;
        this.name = name;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registration_number) {
        this.registrationNumber = registration_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
