package nwt.kts.backend.dto.returnDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import nwt.kts.backend.entity.Drive;
import nwt.kts.backend.entity.Passenger;
import nwt.kts.backend.entity.Status;
import nwt.kts.backend.entity.Tag;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DriveDTO {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("startDate")
    private Timestamp startDate;

    @JsonProperty("endDate")
    private Timestamp endDate;

    @JsonProperty("price")
    private double price;

    @JsonProperty("length")
    private double length;

    @JsonProperty("inconsistentDriveReasoning")
    private List<String> inconsistentDriveReasoning;

    @JsonProperty("tags")
    private List<TagDTO> tags;

    @JsonProperty("status")
    private Status status;

    @JsonProperty("driver")
    private DriverDTO driver;

    @JsonProperty("passengers")
    private List<PassengerDTO> passengers;

    //dodato
    @JsonProperty("route")
    private RouteDTO route;

    public DriveDTO() {
    }

    public DriveDTO(Drive drive) {
        this.id = drive.getId();
        this.startDate = drive.getStartDate();
        this.endDate = drive.getEndDate();
        this.price = drive.getPrice();
        this.length = drive.getLength();
        this.inconsistentDriveReasoning = drive.getInconsistentDriveReasoning();
        this.tags = new ArrayList<>();
        for (Tag tag: drive.getTags()) {
            tags.add(new TagDTO(tag));
        }
        this.status = drive.getStatus();
        this.driver = new DriverDTO(drive.getDriver());
        this.passengers = new ArrayList<>();
        for (Passenger passenger: drive.getPassengers()) this.passengers.add(new PassengerDTO(passenger));
        this.route = new RouteDTO(drive.getRoute());
    }

    public DriveDTO(Drive drive, DriverDTO driverDTO, List<PassengerDTO> passengerDTOS) {
        this.id = drive.getId();
        this.startDate = drive.getStartDate();
        this.endDate = drive.getEndDate();
        this.price = drive.getPrice();
        this.length = drive.getLength();
        this.inconsistentDriveReasoning = drive.getInconsistentDriveReasoning();
        this.tags = new ArrayList<>();
        for (Tag tag: drive.getTags()) {
            tags.add(new TagDTO(tag));
        }
        this.status = drive.getStatus();
        this.driver = driverDTO;
        this.passengers = passengerDTOS;
        this.route = new RouteDTO(drive.getRoute());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<String> getInconsistentDriveReasoning() {
        return inconsistentDriveReasoning;
    }

    public void setInconsistentDriveReasoning(List<String> inconsistentDriveReasoning) {
        this.inconsistentDriveReasoning = inconsistentDriveReasoning;
    }
}
