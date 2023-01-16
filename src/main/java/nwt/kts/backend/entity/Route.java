package nwt.kts.backend.entity;

import nwt.kts.backend.dto.creation.RouteCreationDTO;
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

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "route_waypoints", joinColumns = @JoinColumn(name = "route_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "point_id", referencedColumnName = "id"))
    private Set<Point> waypoints;

    @Column(name = "expected_time", nullable = false)
    private double expectedTime;

    @Column(name = "length", nullable = false)
    private double length;

    @Column(name = "route_idx", nullable = false)
    private int routeIdx;

    public Route() {

    }

    public Route(RouteDTO routeDTO) {
        this.id = routeDTO.getId();
        this.routeName = routeDTO.getRouteName();
        this.expectedTime = routeDTO.getExpectedTime();
        this.length = routeDTO.getLength();
        this.waypoints = routeDTO.getWaypoints().stream().map(Point::new).collect(Collectors.toSet());
        this.routeIdx = routeDTO.getRouteIdx();
    }

    public Route(RouteCreationDTO routeCreationDTO) {
        this.routeName = routeCreationDTO.getRouteName();
        this.expectedTime = routeCreationDTO.getExpectedTime();
        this.length = routeCreationDTO.getLength();
        this.waypoints = routeCreationDTO.getWaypoints().stream().map(Point::new).collect(Collectors.toSet());
        this.routeIdx = routeCreationDTO.getRouteIdx();
    }

    public Route(String routeName, double expectedTime, double length, Set<Point> waypoints, int routeIdx) {
        this.routeName = routeName;
        this.expectedTime = expectedTime;
        this.length = length;
        this.waypoints = waypoints;
        this.routeIdx = routeIdx;
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

    public Set<Point> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(Set<Point> waypoints) {
        this.waypoints = waypoints;
    }

    public int getRouteIdx() {
        return routeIdx;
    }

    public void setRouteIdx(int routeIdx) {
        this.routeIdx = routeIdx;
    }
}
