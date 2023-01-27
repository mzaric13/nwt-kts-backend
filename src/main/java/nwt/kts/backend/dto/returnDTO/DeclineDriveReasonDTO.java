package nwt.kts.backend.dto.returnDTO;

public class DeclineDriveReasonDTO {

    private DriveDTO driveDTO;

    private String reasonOfDeclining;

    public DeclineDriveReasonDTO() {
    }

    public DeclineDriveReasonDTO(DriveDTO driveDTO, String reasonOfDeclining) {
        this.driveDTO = driveDTO;
        this.reasonOfDeclining = reasonOfDeclining;
    }

    public DriveDTO getDriveDTO() {
        return driveDTO;
    }

    public String getReasonOfDeclining() {
        return reasonOfDeclining;
    }
}
