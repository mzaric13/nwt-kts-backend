package nwt.kts.backend.dto.creation;

import java.util.Set;

public class RouteCreationDTO {

    private String routeName;

    private double expectedTime;

    private double length;

    private Set<PointCreationDTO> waypoints;

    private int routeIdx;

    public RouteCreationDTO() {
    }

    public RouteCreationDTO(String routeName, double expectedTime, double length, Set<PointCreationDTO> waypoints, int routeIdx) {
        this.routeName = routeName;
        this.expectedTime = expectedTime;
        this.length = length;
        this.waypoints = waypoints;
        this.routeIdx = routeIdx;
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

    public Set<PointCreationDTO> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(Set<PointCreationDTO> waypoints) {
        this.waypoints = waypoints;
    }

    public int getRouteIdx() {
        return routeIdx;
    }

    public void setRouteIdx(int routeIdx) {
        this.routeIdx = routeIdx;
    }
}
