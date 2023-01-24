package nwt.kts.backend.controller;

import nwt.kts.backend.dto.creation.*;
import nwt.kts.backend.dto.returnDTO.*;
import nwt.kts.backend.entity.Driver;
import nwt.kts.backend.entity.DriverData;
import nwt.kts.backend.entity.Passenger;
import nwt.kts.backend.entity.Point;
import nwt.kts.backend.service.DriverService;
import nwt.kts.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value="/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @Autowired
    private UserService userService;

    @PostMapping(value="/register")
    public ResponseEntity<DriverDTO> registerDriver(@RequestBody DriverCreationDTO driverCreationDTO) {
        Driver driver = driverService.createDriver(driverCreationDTO);
        return new ResponseEntity<>(new DriverDTO(driver), HttpStatus.CREATED);
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<DriverDTO>> getAllDrivers() {
        List<Driver> drivers = driverService.getAllDrivers();
        List<DriverDTO> driverReturnDTOS = drivers.stream().map(DriverDTO::new).collect(Collectors.toList());
        return new ResponseEntity<>(driverReturnDTOS, HttpStatus.OK);
    }
    
    @PostMapping(value="/send-update-request")
    public ResponseEntity<DriverDataDTO> sendUpdateRequest(@RequestBody UpdatedUserDataCreationDTO updatedUserDataCreationDTO){
        DriverData driverData = driverService.sendUpdateRequest(updatedUserDataCreationDTO);
        return new ResponseEntity<>(new DriverDataDTO(driverData), HttpStatus.CREATED);
    }

    @PutMapping(value="/change-password")
    public ResponseEntity<DriverDTO> changePassword(@RequestBody PasswordChangeCreationDTO passwordChangeCreationDTO){
        Driver driver = driverService.changePassword(passwordChangeCreationDTO);
        return new ResponseEntity<>(new DriverDTO(driver), HttpStatus.OK);
    }

    @PutMapping(value="/change-profile-picture")
    public ResponseEntity<DriverDTO> changeProfilePicture(@RequestBody ProfilePictureCreationDTO profilePictureCreationDTO){
        Driver driver = driverService.changeProfilePicture(profilePictureCreationDTO);
        return new ResponseEntity<>(new DriverDTO(driver), HttpStatus.OK);
    }

    @GetMapping(value = "/get-logged")
    public ResponseEntity<DriverDTO> getLoggedDriver(Principal principal) {
        Driver driver = driverService.findDriverByEmail(principal.getName());
        return new ResponseEntity<>(new DriverDTO(driver), HttpStatus.OK);
    }

    @GetMapping(value="/is-unanswered-driver-data-present/{email}")
    public ResponseEntity<DriverDataAnsweredDTO> isUnansweredDriverDataPresent(@PathVariable String email) {
        DriverData driverData = driverService.findUnansweredDriverData(email);
        if (driverData != null) {
            return new ResponseEntity<>(new DriverDataAnsweredDTO(false), HttpStatus.OK);
        }
        return new ResponseEntity<>(new DriverDataAnsweredDTO(true), HttpStatus.OK);
    }

    @PostMapping(value= "/create-driver-chart")
    public ResponseEntity<ChartCreationDTO> createDriverChart(Principal principal, @RequestBody DatesChartDTO datesChartDTO) {
        Driver driver = driverService.findDriverByEmail(principal.getName());
        ChartCreationDTO chartCreationDTO = driverService.createDriverChart(driver, datesChartDTO);
        return new ResponseEntity<>(chartCreationDTO, HttpStatus.OK);
    }

    @GetMapping(value="/change-status")
    public ResponseEntity<DriverDTO> changeDriverStatus(Principal principal) {
        Driver driver = driverService.findDriverByEmail(principal.getName());
        Driver updatedDriver = driverService.changeStatus(driver);
        return new ResponseEntity<>(new DriverDTO(updatedDriver), HttpStatus.OK);
    }

    @GetMapping(value = "/get-by-id/{id}", produces = "application/json")
    public ResponseEntity<DriverDTO> getDriverById(@PathVariable(value = "id") Integer id) {
        Driver driver = driverService.findDriverById(id);
        return new ResponseEntity<>(new DriverDTO(driver), HttpStatus.OK);
    }

    @PutMapping(value = "/update-coordinates/{id}")
    public ResponseEntity<DriverDTO> updateDriverPosition(@RequestBody PointDTO pointDTO, @PathVariable(value = "id") Integer id) {
        Driver driver = driverService.updateDriverPosition(id, pointDTO);
        // TODO: socket call for frontend
        return new ResponseEntity<>(new DriverDTO(driver), HttpStatus.OK);
    }

    @GetMapping(value = "/closest-stop/{id}", produces = "application/json")
    public ResponseEntity<PointDTO> getClosestTaxiStop(@PathVariable("id") Integer id) {
        System.out.println("asasgasgasg");
        Point closestStation = driverService.findClosestTaxiStop(id);
        return new ResponseEntity<>(new PointDTO(closestStation), HttpStatus.OK);
    }
}
