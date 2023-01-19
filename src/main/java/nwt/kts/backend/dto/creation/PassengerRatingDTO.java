package nwt.kts.backend.dto.creation;

public class PassengerRatingDTO {

    private int driveId;
    private boolean canRate;

    public PassengerRatingDTO() {

    }

    public PassengerRatingDTO(int driveId, boolean canRate) {
        this.driveId = driveId;
        this.canRate = canRate;
    }

    public int getDriveId() {
        return driveId;
    }

    public void setDriveId(int driveId) {
        this.driveId = driveId;
    }

    public boolean isCanRate() {
        return canRate;
    }

    public void setCanRate(boolean canRate) {
        this.canRate = canRate;
    }
}
