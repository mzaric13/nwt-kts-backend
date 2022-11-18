package nwt.kts.backend.entity;

import nwt.kts.backend.dto.returnDTO.RouteDTO;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name="route_name")
    private String routeName;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="start_point")
    private Point startPoint;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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

    public Route(RouteDTO routeDTO) {
        this.routeName = routeDTO.getRouteName();
        this.startPoint = new Point(routeDTO.getStartPoint());
        this.endPoint = new Point(routeDTO.getEndPoint());
        this.expectedTime = routeDTO.getExpectedTime();
        this.length = routeDTO.getLength();
        this.routePath = routeDTO.getRoutePath().stream().map(Point::new).collect(Collectors.toSet());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }

    public double getExpectedTime() {
        return expectedTime;
    }

    public void setExpectedTime(double expectedTime) {
        this.expectedTime = expectedTime;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public Set<Point> getRoutePath() {
        return routePath;
    }

    public void setRoutePath(Set<Point> routePath) {
        this.routePath = routePath;
    }
}
