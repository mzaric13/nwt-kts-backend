package nwt.kts.backend.entity;

import nwt.kts.backend.dto.creation.TempDriveDTO;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "temp_drives")
public class TempDrive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "start_date")
    private Timestamp startDate;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "length", nullable = false)
    private double length;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "temp_drive_tags", joinColumns = @JoinColumn(name = "temp_drive_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"))
    private Set<Tag> tags;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "temp_drive_passengers", joinColumns = @JoinColumn(name = "temp_drive_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "passenger_id", referencedColumnName = "id"))
    private Set<Passenger> passengers;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "route_id")
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "vehicle_type_id")
    private Type vehicleType;

    public TempDrive() {
    }

    public TempDrive(Timestamp startDate, double price, double length, Set<Tag> tags, Set<Passenger> passengers, Route route, Type type) {
        this.startDate = startDate;
        this.price = price;
        this.length = length;
        this.tags = tags;
        this.passengers = passengers;
        this.route = route;
        this.vehicleType = type;
    }

    public TempDrive(TempDriveDTO tempDriveDTO, Set<Passenger> passengers, Type type) {
        this.startDate = tempDriveDTO.getStartDate();
        this.price = tempDriveDTO.getPrice();
        this.length = tempDriveDTO.getLength();
        this.tags = tempDriveDTO.getTags().stream().map(Tag::new).collect(Collectors.toSet());
        this.passengers = passengers;
        this.route = new Route(tempDriveDTO.getRouteDTO());
        this.vehicleType = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Set<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(Set<Passenger> passengers) {
        this.passengers = passengers;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Type getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(Type vehicleType) {
        this.vehicleType = vehicleType;
    }
}
