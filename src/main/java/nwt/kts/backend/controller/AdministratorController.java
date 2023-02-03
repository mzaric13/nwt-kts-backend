package nwt.kts.backend.controller;

import nwt.kts.backend.dto.creation.*;
import nwt.kts.backend.dto.returnDTO.*;
import nwt.kts.backend.entity.*;
import nwt.kts.backend.service.AdministratorService;
import nwt.kts.backend.service.UserService;
import nwt.kts.backend.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/administrators")
@PreAuthorize("hasRole('ADMIN')")
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
        if (administrator.getProfilePictureData() != null) {
            ImageDataDTO imageDataDTO = administratorService.createImageDataForAdmin(administrator);
            return new ResponseEntity<>(new AdminDTO(administrator, imageDataDTO), HttpStatus.OK);
        }
        return new ResponseEntity<>(new AdminDTO(administrator), HttpStatus.OK);
    }

    @PutMapping(value = "/change-profile-picture")
    public ResponseEntity<AdminDTO> changeProfilePicture(@RequestParam("image") MultipartFile file, Principal principal) throws IOException {
        User user = administratorService.changeProfilePicture(principal.getName(), file);
        if (user.getProfilePictureData() != null) {
            ImageDataDTO imageDataDTO = administratorService.createImageDataForAdmin(user);
            return new ResponseEntity<>(new AdminDTO(user, imageDataDTO), HttpStatus.OK);
        }
        return new ResponseEntity<>(new AdminDTO(user), HttpStatus.OK);
    }

    @PutMapping(value = "/update-personal-info")
    public ResponseEntity<AdminDTO> changePersonalInfo(@RequestBody AdminDTO userReturnDTO) {
        User user = administratorService.changePersonalInfo(userReturnDTO);
        if (user.getProfilePictureData() != null) {
            ImageDataDTO imageDataDTO = administratorService.createImageDataForAdmin(user);
            return new ResponseEntity<>(new AdminDTO(user, imageDataDTO), HttpStatus.OK);
        }
        return new ResponseEntity<>(new AdminDTO(user), HttpStatus.OK);
    }

    @GetMapping(value = "/get-all-passengers")
    public ResponseEntity<Map<String, Object>> getAllPassengers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Page<Passenger> passengers = administratorService.getAllPassengers(page, size);
        return new ResponseEntity<>(createPassengerResponse(passengers), HttpStatus.OK);
    }

    @GetMapping(value = "/get-all-active-passengers")
    public ResponseEntity<Map<String, Object>> getAllActivePassengers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Page<Passenger> passengers = administratorService.getAllActivePassengers(page, size);
        return new ResponseEntity<>(createPassengerResponse(passengers), HttpStatus.OK);
    }

    @GetMapping(value = "/get-all-drivers")
    public ResponseEntity<Map<String, Object>> getAllDrivers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Page<Driver> drivers = administratorService.getAllDrivers(page, size);
        return new ResponseEntity<>(createDriverResponse(drivers), HttpStatus.OK);
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
        if (administrator.getProfilePictureData() != null) {
            ImageDataDTO imageDataDTO = administratorService.createImageDataForAdmin(administrator);
            return new ResponseEntity<>(new AdminDTO(administrator, imageDataDTO), HttpStatus.OK);
        }
        return new ResponseEntity<>(new AdminDTO(administrator), HttpStatus.OK);
    }

    @PostMapping(value = "/create-admin-chart")
    public ResponseEntity<ChartCreationDTO> createAdminChart(@RequestBody DatesChartDTO datesChartDTO) {
        ChartCreationDTO chartCreationDTO = administratorService.createAdminChart(datesChartDTO);
        return new ResponseEntity<>(chartCreationDTO, HttpStatus.OK);
    }

    private Map<String, Object> createDriverDataResponse(Page<DriverData> driverDataPage) {
        Map<String, Object> returnValue = new HashMap<>();
        List<DriverDataDTO> driverDataDTOS = new ArrayList<>();
        for (DriverData driverData : driverDataPage.getContent()) {
            driverDataDTOS.add(new DriverDataDTO(driverData));
        }
        returnValue.put("driverData", driverDataDTOS);
        returnValue.put("totalItems", driverDataPage.getTotalElements());
        returnValue.put("totalPages", driverDataPage.getTotalPages());
        return returnValue;
    }

    private Map<String, Object> createPassengerResponse(Page<Passenger> passengersPage) {
        Map<String, Object> returnValue = new HashMap<>();
        List<PassengerDTO> passengerDTOS = new ArrayList<>();
        for (Passenger passenger : passengersPage.getContent()) {
            passengerDTOS.add(new PassengerDTO(passenger));
        }
        returnValue.put("passengers", passengerDTOS);
        returnValue.put("totalItems", passengersPage.getTotalElements());
        returnValue.put("totalPages", passengersPage.getTotalPages());
        return returnValue;
    }

    private Map<String, Object> createDriverResponse(Page<Driver> driversPage) {
        Map<String, Object> returnValue = new HashMap<>();
        List<DriverDTO> driverDTOS = new ArrayList<>();
        for (Driver driver : driversPage.getContent()) {
            driverDTOS.add(new DriverDTO(driver));
        }
        returnValue.put("drivers", driverDTOS);
        returnValue.put("totalItems", driversPage.getTotalElements());
        returnValue.put("totalPages", driversPage.getTotalPages());
        return returnValue;
    }
}
