package nwt.kts.backend.dto.returnDTO;

import nwt.kts.backend.entity.Route;

import java.util.Set;
import java.util.stream.Collectors;

public class RouteDTO {

    private int id;
    private String routeName;
    private double expectedTime;
    private double length;
    private Set<PointDTO> waypoints;

    private int routeIdx;

    public RouteDTO(int id, String routeName, double expectedTime, double length, Set<PointDTO> waypoints, int routeIdx) {
        this.id = id;
        this.routeName = routeName;
        this.expectedTime = expectedTime;
        this.length = length;
        this.waypoints = waypoints;
        this.routeIdx = routeIdx;
    }

    public RouteDTO() {
    }

    public RouteDTO(Route route) {
        this.id = route.getId();
        this.routeName = route.getRouteName();
        this.expectedTime = route.getExpectedTime();
        this.length = route.getLength();
        this.waypoints = route.getWaypoints().stream().map(PointDTO::new).collect(Collectors.toSet());
        this.routeIdx = route.getRouteIdx();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public Set<PointDTO> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(Set<PointDTO> waypoints) {
        this.waypoints = waypoints;
    }

    public int getRouteIdx() {
        return routeIdx;
    }

    public void setRouteIdx(int routeIdx) {
        this.routeIdx = routeIdx;
    }
}
