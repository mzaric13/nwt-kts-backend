package nwt.kts.backend.dto.returnDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import nwt.kts.backend.entity.Drive;
import nwt.kts.backend.entity.Passenger;
import nwt.kts.backend.entity.Status;
import nwt.kts.backend.entity.Tag;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
    private String inconsistentDriveReasoning;

    @JsonProperty("tags")
    private List<TagDTO> tags;

    @JsonProperty("status")
    private Status status;

    @JsonProperty("driver")
    private DriverDTO driver;

    @JsonProperty("passengers")
    private List<PassengerDTO> passengers;

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


}
