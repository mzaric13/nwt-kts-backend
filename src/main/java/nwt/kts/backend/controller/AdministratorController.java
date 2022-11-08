package nwt.kts.backend.controller;

import nwt.kts.backend.dto.creation.AnsweredDriverDataCreationDTO;
import nwt.kts.backend.dto.creation.PasswordChangeCreationDTO;
import nwt.kts.backend.dto.creation.ProfilePictureCreationDTO;
import nwt.kts.backend.dto.returnDTO.DriverDataReturnDTO;
import nwt.kts.backend.dto.returnDTO.DriverReturnDTO;
import nwt.kts.backend.dto.returnDTO.PassengerDTO;
import nwt.kts.backend.dto.returnDTO.UserReturnDTO;
import nwt.kts.backend.entity.Driver;
import nwt.kts.backend.entity.DriverData;
import nwt.kts.backend.entity.Passenger;
import nwt.kts.backend.entity.User;
import nwt.kts.backend.service.AdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/administrators")
public class AdministratorController {

    @Autowired
    private AdministratorService administratorService;

    @GetMapping(value = "/get-unanswered-driver-data")
    public ResponseEntity<List<DriverDataReturnDTO>> getUnansweredDriverData() {
        List<DriverData> unansweredDriverData = administratorService.getUnansweredDriverData();
        List<DriverDataReturnDTO> unansweredDriverDataReturnDTOs = new ArrayList<>();
        for (DriverData driverData : unansweredDriverData) {
            unansweredDriverDataReturnDTOs.add(new DriverDataReturnDTO(driverData));
        }
        return new ResponseEntity<>(unansweredDriverDataReturnDTOs, HttpStatus.FOUND);
    }

    @PutMapping(value = "/answer-driver-data-change")
    public ResponseEntity<DriverDataReturnDTO> answerDriverDataChange(@RequestBody AnsweredDriverDataCreationDTO answeredDriverDataCreationDTO) {
        DriverData driverData = administratorService.answerDriverDataChange(answeredDriverDataCreationDTO);
        return new ResponseEntity<>(new DriverDataReturnDTO(driverData), HttpStatus.OK);
    }

    @PutMapping(value = "/change-password")
    public ResponseEntity<UserReturnDTO> changePassword(@RequestBody PasswordChangeCreationDTO passwordChangeCreationDTO) {
        User administrator = administratorService.changePassword(passwordChangeCreationDTO);
        return new ResponseEntity<>(new UserReturnDTO(administrator), HttpStatus.OK);
    }

    @PutMapping(value = "/change-profile-picture")
    public ResponseEntity<UserReturnDTO> changeProfilePicture(@RequestBody ProfilePictureCreationDTO profilePictureCreationDTO) {
        User user = administratorService.changeProfilePicture(profilePictureCreationDTO);
        return new ResponseEntity<>(new UserReturnDTO(user), HttpStatus.OK);
    }

    @PutMapping(value = "/update-personal-info")
    public ResponseEntity<UserReturnDTO> changePersonalInfo(@RequestBody UserReturnDTO userReturnDTO) {
        User user = administratorService.changePersonalInfo(userReturnDTO);
        return new ResponseEntity<>(new UserReturnDTO(user), HttpStatus.OK);
    }

    @GetMapping(value = "/get-all-not-blocked-passengers")
    public ResponseEntity<List<PassengerDTO>> getAllNotBlockedPassengers() {
        List<Passenger> passengers = administratorService.getAllNotBlockedPassengers();
        List<PassengerDTO> passengerDTOs = new ArrayList<>();
        for (Passenger passenger : passengers) {
            passengerDTOs.add(new PassengerDTO(passenger));
        }
        return new ResponseEntity<>(passengerDTOs, HttpStatus.FOUND);
    }

    @GetMapping(value = "/get-all-not-blocked-drivers")
    public ResponseEntity<List<DriverReturnDTO>> getAllNotBlockedDrivers() {
        List<Driver> drivers = administratorService.getAllNotBlockedDrivers();
        List<DriverReturnDTO> driverReturnDTOs = new ArrayList<>();
        for (Driver driver : drivers) {
            driverReturnDTOs.add(new DriverReturnDTO(driver));
        }
        return new ResponseEntity<>(driverReturnDTOs, HttpStatus.FOUND);
    }

    @PutMapping(value = "/block-passenger/{email}")
    public ResponseEntity<PassengerDTO> blockPassenger(@PathVariable String email) {
        Passenger passenger = administratorService.blockPassenger(email);
        return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
    }

    @PutMapping(value = "/block-driver/{email}")
    public ResponseEntity<DriverReturnDTO> blockDriver(@PathVariable String email) {
        Driver driver = administratorService.blockDriver(email);
        return new ResponseEntity<>(new DriverReturnDTO(driver), HttpStatus.OK);
    }
}
