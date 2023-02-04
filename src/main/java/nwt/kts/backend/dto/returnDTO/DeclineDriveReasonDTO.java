package nwt.kts.backend.dto.returnDTO;

public class DeclineDriveReasonDTO {

    private DriveDTO driveDTO;

    private String reasonForDeclining;

    public DeclineDriveReasonDTO() {
    }

    public DeclineDriveReasonDTO(DriveDTO driveDTO, String reasonForDeclining) {
        this.driveDTO = driveDTO;
        this.reasonForDeclining = reasonForDeclining;
    }

    public DriveDTO getDriveDTO() {
        return driveDTO;
    }

    public String getReasonForDeclining() {
        return reasonForDeclining;
    }
}
