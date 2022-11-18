package nwt.kts.backend.entity;

import javax.persistence.*;

@Entity
@Table(name="points",
        uniqueConstraints = {
        @UniqueConstraint(name = "UniqueLatitudeAndLongitude", columnNames = {"latitude", "longitude"})},
        indexes = { @Index(name = "points_latitude_longitude_idx", columnList = "latitude, longitude")})
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    public Point() {

    }

}
