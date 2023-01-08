package nwt.kts.backend.dto.creation;

import java.util.Set;

public class RouteCreationDTO {

    private String routeName;

    private PointCreationDTO startPoint;

    private PointCreationDTO endPoint;

    private double expectedTime;

    private double length;

    private Set<PointCreationDTO> routePath;

    public RouteCreationDTO() {
    }

    public RouteCreationDTO(String routeName, PointCreationDTO startPoint, PointCreationDTO endPoint, double expectedTime,
                            double length, Set<PointCreationDTO> routePath) {
        this.routeName = routeName;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.expectedTime = expectedTime;
        this.length = length;
        this.routePath = routePath;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public PointCreationDTO getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(PointCreationDTO startPoint) {
        this.startPoint = startPoint;
    }

    public PointCreationDTO getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(PointCreationDTO endPoint) {
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

    public Set<PointCreationDTO> getRoutePath() {
        return routePath;
    }

    public void setRoutePath(Set<PointCreationDTO> routePath) {
        this.routePath = routePath;
    }
}
