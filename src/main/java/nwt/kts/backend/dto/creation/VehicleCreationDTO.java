package nwt.kts.backend.dto.creation;

public class VehicleCreationDTO {

    private String registrationNumber;
    private String name;
    private String type;

    public VehicleCreationDTO(){

    }

    public VehicleCreationDTO(String registrationNumber, String name, String type) {
        this.registrationNumber = registrationNumber;
        this.name = name;
        this.type = type;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
