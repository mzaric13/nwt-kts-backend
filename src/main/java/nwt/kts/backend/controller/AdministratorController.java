package nwt.kts.backend.controller;

import nwt.kts.backend.dto.creation.AnsweredDriverDataCreationDTO;
import nwt.kts.backend.dto.creation.PasswordChangeCreationDTO;
import nwt.kts.backend.dto.creation.ProfilePictureCreationDTO;
import nwt.kts.backend.dto.creation.UserIdDTO;
import nwt.kts.backend.dto.returnDTO.DriverDataDTO;
import nwt.kts.backend.dto.returnDTO.DriverDTO;
import nwt.kts.backend.dto.returnDTO.PassengerDTO;
import nwt.kts.backend.dto.returnDTO.AdminDTO;
import nwt.kts.backend.entity.Driver;
import nwt.kts.backend.entity.DriverData;
import nwt.kts.backend.entity.Passenger;
import nwt.kts.backend.entity.User;
import nwt.kts.backend.service.AdministratorService;
import nwt.kts.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/administrators")
public class AdministratorController {

    @Autowired
    private AdministratorService administratorService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "/get-unanswered-driver-data")
    public ResponseEntity<List<DriverDataDTO>> getUnansweredDriverData() {
        List<DriverData> unansweredDriverData = administratorService.getUnansweredDriverData();
        List<DriverDataDTO> unansweredDriverDataDTOs = new ArrayList<>();
        for (DriverData driverData : unansweredDriverData) {
            unansweredDriverDataDTOs.add(new DriverDataDTO(driverData));
        }
        return new ResponseEntity<>(unansweredDriverDataDTOs, HttpStatus.OK);
    }

    @PutMapping(value = "/answer-driver-data-change")
    public ResponseEntity<DriverDataDTO> answerDriverDataChange(@RequestBody AnsweredDriverDataCreationDTO answeredDriverDataCreationDTO) {
        DriverData driverData = administratorService.answerDriverDataChange(answeredDriverDataCreationDTO);
        return new ResponseEntity<>(new DriverDataDTO(driverData), HttpStatus.OK);
    }

    @PutMapping(value = "/change-password")
    public ResponseEntity<AdminDTO> changePassword(@RequestBody PasswordChangeCreationDTO passwordChangeCreationDTO) {
        User administrator = administratorService.changePassword(passwordChangeCreationDTO);
        return new ResponseEntity<>(new AdminDTO(administrator), HttpStatus.OK);
    }

    @PutMapping(value = "/change-profile-picture")
    public ResponseEntity<AdminDTO> changeProfilePicture(@RequestBody ProfilePictureCreationDTO profilePictureCreationDTO) {
        User user = administratorService.changeProfilePicture(profilePictureCreationDTO);
        return new ResponseEntity<>(new AdminDTO(user), HttpStatus.OK);
    }

    @PutMapping(value = "/update-personal-info")
    public ResponseEntity<AdminDTO> changePersonalInfo(@RequestBody AdminDTO userReturnDTO) {
        User user = administratorService.changePersonalInfo(userReturnDTO);
        return new ResponseEntity<>(new AdminDTO(user), HttpStatus.OK);
    }

    @GetMapping(value = "/get-all-passengers")
    public ResponseEntity<List<PassengerDTO>> getAllPassengers() {
        List<Passenger> passengers = administratorService.getAllPassengers();
        List<PassengerDTO> passengerDTOs = new ArrayList<>();
        for (Passenger passenger : passengers) {
            passengerDTOs.add(new PassengerDTO(passenger));
        }
        return new ResponseEntity<>(passengerDTOs, HttpStatus.OK);
    }

    @GetMapping(value = "/get-all-drivers")
    public ResponseEntity<List<DriverDTO>> getAllDrivers() {
        List<Driver> drivers = administratorService.getAllDrivers();
        List<DriverDTO> driverReturnDTOs = new ArrayList<>();
        for (Driver driver : drivers) {
            driverReturnDTOs.add(new DriverDTO(driver));
        }
        return new ResponseEntity<>(driverReturnDTOs, HttpStatus.OK);
    }

    @PutMapping(value = "/change-block-status-passenger")
    public ResponseEntity<PassengerDTO> changeBlockStatusPassenger(@RequestBody UserIdDTO userIdDTO) {
        Passenger passenger = administratorService.changeBlockedStatusPassenger(userIdDTO);
        return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
    }

    @PutMapping(value = "/change-block-status-driver")
    public ResponseEntity<DriverDTO> changeBlockStatusDriver(@RequestBody UserIdDTO userIdDTO) {
        Driver driver = administratorService.changeBlockedStatusDriver(userIdDTO);
        return new ResponseEntity<>(new DriverDTO(driver), HttpStatus.OK);
    }

    @GetMapping(value = "/get-logged")
    public ResponseEntity<AdminDTO> getLoggedAdmin(Principal principal) {
        User administrator = userService.findUserByEmail(principal.getName());
        return new ResponseEntity<>(new AdminDTO(administrator), HttpStatus.OK);
    }
}
