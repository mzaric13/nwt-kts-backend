package nwt.kts.backend.entity;

import javax.persistence.*;

@Entity
@Table(name="points")
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "latitude", unique = true)
    private double latitude;

    @Column(name = "longitude", unique = true)
    private double longitude;

    public Point() {

    }

}
