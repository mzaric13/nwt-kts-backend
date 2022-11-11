package nwt.kts.backend.entity;

import javax.persistence.*;
import java.awt.*;
import java.util.Set;

@Entity
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name="route_name")
    private String routeName;

    @Column(name="start_point")
    private Point start;

    @Column(name="end_point")
    private Point endPoint;

    @Column(name = "expected_time", nullable = false)
    private double expectedTime;

    @Column(name = "length", nullable = false)
    private double length;

    @ElementCollection
    @CollectionTable(name = "route_points", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "route_path", nullable = false)
    private Set<Point> routePath;

    public Route() {

    }

}
