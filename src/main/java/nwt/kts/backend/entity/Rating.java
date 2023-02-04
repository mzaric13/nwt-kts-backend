package nwt.kts.backend.entity;

import nwt.kts.backend.dto.creation.RatingCreationDTO;

import javax.persistence.*;

// podesiti da su drive i passenger PRIMARY key, kako bi se ogranicilo da passenger moze da napise tacno jedan rating

@Entity
@Table(name = "ratings")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "driver_rating", nullable = false)
    private Integer driverRating;

    @Column(name = "vehicle_rating", nullable = false)
    private Integer vehicleRating;

    @Column(name = "comment")
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drive_id")
    private Drive drive;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;

    public Rating() {

    }

    public Rating(Integer id, Integer driverRating, Integer vehicleRating, String comment, Drive drive, Passenger passenger) {
        this.id = id;
        this.driverRating = driverRating;
        this.vehicleRating = vehicleRating;
        this.comment = comment;
        this.drive = drive;
        this.passenger = passenger;
    }

    public Rating(RatingCreationDTO ratingCreationDTO, Drive drive, Passenger passenger) {
        this.driverRating = ratingCreationDTO.getDriverRating();
        this.vehicleRating = ratingCreationDTO.getVehicleRating();
        this.comment = ratingCreationDTO.getComment();
        this.drive = drive;
        this.passenger = passenger;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(Integer driverRating) {
        this.driverRating = driverRating;
    }

    public Integer getVehicleRating() {
        return vehicleRating;
    }

    public void setVehicleRating(Integer vehicleRating) {
        this.vehicleRating = vehicleRating;
    }

    public Drive getDrive() {
        return drive;
    }

    public void setDrive(Drive drive) {
        this.drive = drive;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
