package nwt.kts.backend.dto.creation;

public class AnsweredDriverDataCreationDTO {

    public Integer driverDataId;
    public boolean isApproved;

    public AnsweredDriverDataCreationDTO() {

    }

    public AnsweredDriverDataCreationDTO(Integer driverDataId, boolean isApproved) {
        this.driverDataId = driverDataId;
        this.isApproved = isApproved;
    }

    public Integer getDriverDataId() {
        return driverDataId;
    }

    public void setDriverDataId(Integer driverDataId) {
        this.driverDataId = driverDataId;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setIsApproved(boolean approved) {
        isApproved = approved;
    }
}
