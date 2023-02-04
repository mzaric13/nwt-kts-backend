package nwt.kts.backend.dto.returnDTO;

import nwt.kts.backend.entity.Rating;

public class RatingDTO {

    private Integer id;
    private Integer driverRating;
    private Integer vehicleRating;
    private String comment;
    private Integer driveId;
    private Integer passengerId;

    public RatingDTO() {

    }

    public RatingDTO(Integer id, Integer driverRating, Integer vehicleRating, String comment, Integer driveId, Integer passengerId) {
        this.id = id;
        this.driverRating = driverRating;
        this.vehicleRating = vehicleRating;
        this.comment = comment;
        this.driveId = driveId;
        this.passengerId = passengerId;
    }

    public RatingDTO(Rating rating) {
        this.id = rating.getId();
        this.driverRating = rating.getDriverRating();
        this.vehicleRating = rating.getVehicleRating();
        this.comment = rating.getComment();
        this.driveId = rating.getDrive().getId();
        this.passengerId = rating.getPassenger().getId();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
