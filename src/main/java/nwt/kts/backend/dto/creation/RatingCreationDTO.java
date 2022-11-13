package nwt.kts.backend.dto.creation;

public class RatingCreationDTO {

    private Integer driverRating;
    private Integer vehicleRating;
    private Integer driveId;
    private Integer passengerId;

    public RatingCreationDTO() {

    }

    public RatingCreationDTO(Integer driverRating, Integer vehicleRating, Integer driveId, Integer passengerId) {
        this.driverRating = driverRating;
        this.vehicleRating = vehicleRating;
        this.driveId = driveId;
        this.passengerId = passengerId;
    }

    public Integer getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(Integer driverRating) {
        this.driverRating = driverRating;
    }

    public Integer getVehicleRating() {
        return vehicleRating;
    }

    public void setVehicleRating(Integer vehicleRating) {
        this.vehicleRating = vehicleRating;
    }

    public Integer getDriveId() {
        return driveId;
    }

    public void setDriveId(Integer driveId) {
        this.driveId = driveId;
    }

    public Integer getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Integer passengerId) {
        this.passengerId = passengerId;
    }
}
