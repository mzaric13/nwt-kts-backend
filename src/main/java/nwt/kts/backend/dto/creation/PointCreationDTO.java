package nwt.kts.backend.dto.creation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PointCreationDTO {

    private double latitude;

    private double longitude;

    public PointCreationDTO() {
    }

    public PointCreationDTO(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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
