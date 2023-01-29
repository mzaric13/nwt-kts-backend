package nwt.kts.backend.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    @ElementCollection
    @CollectionTable(name = "inconsistent_drive_reasonings", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "inconsistent_drive_reasoning")
    private List<String> inconsistentDriveReasoning = new ArrayList<>();

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    public Drive() {

    }

    public Drive(Integer id, Timestamp startDate, Timestamp endDate, double price, double length,
                 List<String> inconsistentDriveReasoning, Set<Tag> tags, Status status, Driver driver, Set<Passenger> passengers, Route route) {
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
        this.route = route;
    }

    public Drive(TempDrive tempDrive, Driver driver) {
        this.startDate = tempDrive.getStartDate();
        this.price = tempDrive.getPrice();
        this.length = tempDrive.getLength();
        this.tags = new HashSet<>(tempDrive.getTags());
        if (tempDrive.getStatus() == Status.RESERVED) this.status = Status.PAID_RESERVED;
        else this.status = Status.PAID;
        this.passengers = new HashSet<>(tempDrive.getPassengers());
        this.route = tempDrive.getRoute();
        this.driver = driver;
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

    public List<String> getInconsistentDriveReasoning() {
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

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setInconsistentDriveReasoning(List<String> inconsistentDriveReasoning) {
        this.inconsistentDriveReasoning = inconsistentDriveReasoning;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }
}
