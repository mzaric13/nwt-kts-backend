package nwt.kts.backend.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "drives")
public class Drive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "start_date")
    private Timestamp startDate;

    @Column(name = "end_date")
    private Timestamp endDate;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "length", nullable = false)
    private double length;

    @Column(name = "inconsistent_drive_reasoning")
    private String inconsistentDriveReasoning;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "drive_tags", joinColumns = @JoinColumn(name = "drive_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"))
    private Set<Tag> tags;

    @Column(name = "status")
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver")
    private Driver driver;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "drive_passengers", joinColumns = @JoinColumn(name = "drive_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "passenger_id", referencedColumnName = "id"))
    private Set<Passenger> passengers;

    //TODO
    //Route?

    public Drive() {

    }

    public Drive(Integer id, Timestamp startDate, Timestamp endDate, double price, double length,
                 String inconsistentDriveReasoning, Set<Tag> tags, Status status, Driver driver, Set<Passenger> passengers) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.length = length;
        this.inconsistentDriveReasoning = inconsistentDriveReasoning;
        this.tags = tags;
        this.status = status;
        this.driver = driver;
        this.passengers = passengers;
    }

    public Integer getId() {
        return id;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public double getPrice() {
        return price;
    }

    public double getLength() {
        return length;
    }

    public String getInconsistentDriveReasoning() {
        return inconsistentDriveReasoning;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public Status getStatus() {
        return status;
    }

    public Driver getDriver() {
        return driver;
    }

    public Set<Passenger> getPassengers() {
        return passengers;
    }
}
