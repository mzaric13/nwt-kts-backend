package nwt.kts.backend.entity;

import nwt.kts.backend.dto.returnDTO.PointDTO;

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

    public Point(PointDTO pointDTO) {
        this.latitude = pointDTO.getLatitude();
        this.longitude = pointDTO.getLongitude();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
