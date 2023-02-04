package nwt.kts.backend.dto.returnDTO;

import nwt.kts.backend.entity.Vehicle;

public class VehicleReturnDTO {

    private Integer id;
    private String registrationNumber;
    private String name;
    private String type;

    public VehicleReturnDTO(){

    }

    public VehicleReturnDTO(Integer id, String registrationNumber, String name, String type) {
        this.id = id;
        this.registrationNumber = registrationNumber;
        this.name = name;
        this.type = type;
    }

    public VehicleReturnDTO(Vehicle vehicle){
        this.id = vehicle.getId();
        this.registrationNumber = vehicle.getRegistrationNumber();
        this.name = vehicle.getName();
        this.type = vehicle.getType().getName();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
