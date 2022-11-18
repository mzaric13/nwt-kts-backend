package nwt.kts.backend.dto.returnDTO;

import nwt.kts.backend.entity.Route;

import java.util.Set;
import java.util.stream.Collectors;

public class RouteDTO {

    private int id;
    private String routeName;
    private PointDTO startPoint;
    private PointDTO endPoint;
    private double expectedTime;
    private double length;
    private Set<PointDTO> routePath;

    public RouteDTO(int id, String routeName, PointDTO startPoint, PointDTO endPoint, double expectedTime, double length, Set<PointDTO> routePath) {
        this.id = id;
        this.routeName = routeName;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.expectedTime = expectedTime;
        this.length = length;
        this.routePath = routePath;
    }

    public RouteDTO() {
    }

    public RouteDTO(Route route) {
        this.id = route.getId();
        this.routeName = route.getRouteName();
        this.startPoint = new PointDTO(route.getStartPoint());
        this.endPoint = new PointDTO(route.getEndPoint());
        this.expectedTime = route.getExpectedTime();
        this.length = route.getLength();
        this.routePath = route.getRoutePath().stream().map(PointDTO::new).collect(Collectors.toSet());
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

    public PointDTO getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(PointDTO startPoint) {
        this.startPoint = startPoint;
    }

    public PointDTO getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(PointDTO endPoint) {
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

    public Set<PointDTO> getRoutePath() {
        return routePath;
    }

    public void setRoutePath(Set<PointDTO> routePath) {
        this.routePath = routePath;
    }
}
