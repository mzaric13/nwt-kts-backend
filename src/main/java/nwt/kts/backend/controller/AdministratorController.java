package nwt.kts.backend.controller;

import nwt.kts.backend.dto.creation.*;
import nwt.kts.backend.dto.returnDTO.*;
import nwt.kts.backend.entity.*;
import nwt.kts.backend.service.AdministratorService;
import nwt.kts.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/administrators")
public class AdministratorController {

    @Autowired
    private AdministratorService administratorService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "/get-unanswered-driver-data")
    public ResponseEntity<Map<String, Object>> getUnansweredDriverData(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Page<DriverData> unansweredDriverDataPage = administratorService.getUnansweredDriverData(page, size);
        return new ResponseEntity<>(createDriverDataResponse(unansweredDriverDataPage), HttpStatus.OK);
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

    @PostMapping(value= "/create-admin-chart")
    public ResponseEntity<ChartCreationDTO> createAdminChart(@RequestBody DatesChartDTO datesChartDTO) {
        ChartCreationDTO chartCreationDTO = administratorService.createAdminChart(datesChartDTO);
        return new ResponseEntity<>(chartCreationDTO, HttpStatus.OK);
    }

    private Map<String, Object> createDriverDataResponse(Page<DriverData> driverDataPage) {
        Map<String, Object> returnValue = new HashMap<>();
        List<DriverDataDTO> driverDataDTOS = new ArrayList<>();
        for (DriverData driverData: driverDataPage.getContent()) {
            driverDataDTOS.add(new DriverDataDTO(driverData));
        }
        returnValue.put("driverData", driverDataDTOS);
        returnValue.put("totalItems", driverDataPage.getTotalElements());
        returnValue.put("totalPages", driverDataPage.getTotalPages());
        return returnValue;
    }
}
