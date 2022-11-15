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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="start_point")
    private Point startPoint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="end_point")
    private Point endPoint;

    @Column(name = "expected_time", nullable = false)
    private double expectedTime;

    @Column(name = "length", nullable = false)
    private double length;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "route_points", joinColumns = @JoinColumn(name = "route_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "point_id", referencedColumnName = "id"))
    private Set<Point> routePath;

    public Route() {

    }

}
