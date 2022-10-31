package nwt.kts.backend.entity;

import javax.persistence.*;

@Entity
@Table(name="ratings")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    private Integer id;

    @Column(name="driverRating", nullable=false)
    private Integer driverRating;

    @Column(name="vehicleRating", nullable=false)
    private Integer vehicleRating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="driver_id")
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="vehicle_id")
    private Vehicle vehicle;

    public Rating(){

    }

    public Rating(Integer id, Integer driverRating, Integer vehicleRating, Driver driver, Vehicle vehicle) {
        this.id = id;
        this.driverRating = driverRating;
        this.vehicleRating = vehicleRating;
        this.driver = driver;
        this.vehicle = vehicle;
    }
}
