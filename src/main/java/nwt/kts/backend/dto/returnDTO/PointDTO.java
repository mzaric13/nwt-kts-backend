package nwt.kts.backend.dto.returnDTO;

import nwt.kts.backend.entity.Point;

public class PointDTO {
    private int id;
    private double latitude;
    private double longitude;

    public PointDTO() {
    }

    public PointDTO(int id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public PointDTO(Point point) {
        this.id = point.getId();
        this.latitude = point.getLatitude();
        this.longitude = point.getLongitude();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
