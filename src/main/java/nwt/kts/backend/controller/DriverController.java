package nwt.kts.backend.controller;

import nwt.kts.backend.dto.creation.*;
import nwt.kts.backend.dto.returnDTO.*;
import nwt.kts.backend.entity.*;
import nwt.kts.backend.service.DriverService;
import nwt.kts.backend.service.UserService;
import nwt.kts.backend.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping(value="/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DriverDTO> registerDriver(@RequestBody DriverCreationDTO driverCreationDTO) {
        Driver driver = driverService.createDriver(driverCreationDTO);
        simpMessagingTemplate.convertAndSend("/secured/update/newDriver", new DriverDTO(driver));
        return new ResponseEntity<>(new DriverDTO(driver), HttpStatus.CREATED);
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<DriverDTO>> getAllDrivers() {
        List<Driver> drivers = driverService.getAllDrivers();
        List<DriverDTO> driverReturnDTOS = drivers.stream().map(DriverDTO::new).collect(Collectors.toList());
        return new ResponseEntity<>(driverReturnDTOS, HttpStatus.OK);
    }
    
    @PostMapping(value="/send-update-request")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<DriverDataDTO> sendUpdateRequest(@RequestBody UpdatedUserDataCreationDTO updatedUserDataCreationDTO){
        DriverData driverData = driverService.sendUpdateRequest(updatedUserDataCreationDTO);
        return new ResponseEntity<>(new DriverDataDTO(driverData), HttpStatus.CREATED);
    }

    @PutMapping(value="/change-password")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<DriverDTO> changePassword(@RequestBody PasswordChangeCreationDTO passwordChangeCreationDTO){
        Driver driver = driverService.changePassword(passwordChangeCreationDTO);
        if (driver.getProfilePictureData() != null) {
            ImageDataDTO imageDataDTO = driverService.getImageDataForDriver(driver);
            return new ResponseEntity<>(new DriverDTO(driver, imageDataDTO), HttpStatus.OK);
        }
        return new ResponseEntity<>(new DriverDTO(driver), HttpStatus.OK);
    }

    @PutMapping(value="/change-profile-picture")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<DriverDTO> changeProfilePicture(@RequestParam("image") MultipartFile file, Principal principal) throws IOException {
        Driver driver = driverService.changeProfilePicture(principal.getName(), file);
        if (driver.getProfilePictureData() != null) {
            ImageDataDTO imageDataDTO = driverService.getImageDataForDriver(driver);
            return new ResponseEntity<>(new DriverDTO(driver, imageDataDTO), HttpStatus.OK);
        }
        return new ResponseEntity<>(new DriverDTO(driver), HttpStatus.OK);
    }

    @GetMapping(value = "/get-logged")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<DriverDTO> getLoggedDriver(Principal principal) {
        Driver driver = driverService.findDriverByEmail(principal.getName());
        if (driver.getProfilePictureData() != null) {
            ImageDataDTO imageDataDTO = driverService.getImageDataForDriver(driver);
            return new ResponseEntity<>(new DriverDTO(driver, imageDataDTO), HttpStatus.OK);
        }
        return new ResponseEntity<>(new DriverDTO(driver), HttpStatus.OK);
    }

    @GetMapping(value="/is-unanswered-driver-data-present/{email}")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<DriverDataAnsweredDTO> isUnansweredDriverDataPresent(@PathVariable String email) {
        DriverData driverData = driverService.findUnansweredDriverData(email);
        if (driverData != null) {
            return new ResponseEntity<>(new DriverDataAnsweredDTO(false), HttpStatus.OK);
        }
        return new ResponseEntity<>(new DriverDataAnsweredDTO(true), HttpStatus.OK);
    }

    @PostMapping(value= "/create-driver-chart")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ChartCreationDTO> createDriverChart(Principal principal, @RequestBody DatesChartDTO datesChartDTO) {
        Driver driver = driverService.findDriverByEmail(principal.getName());
        ChartCreationDTO chartCreationDTO = driverService.createDriverChart(driver, datesChartDTO);
        return new ResponseEntity<>(chartCreationDTO, HttpStatus.OK);
    }

    @GetMapping(value="/change-status")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<DriverDTO> changeDriverStatus(Principal principal) {
        Driver driver = driverService.findDriverByEmail(principal.getName());
        Driver updatedDriver = driverService.changeStatus(driver);
        simpMessagingTemplate.convertAndSend("/secured/update/driverStatus", new DriverDTO(updatedDriver));
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
        simpMessagingTemplate.convertAndSend("/secured/simulation/update-vehicle-position", new DriverDTO(driver));
        return new ResponseEntity<>(new DriverDTO(driver), HttpStatus.OK);
    }

    @PutMapping(value = "/set-coordinates/{id}")
    public ResponseEntity<DriverDTO> setDriverPosition(@RequestBody PointDTO pointDTO, @PathVariable(value = "id") Integer id) {
        Driver driver = driverService.updateDriverPosition(id, pointDTO);
        simpMessagingTemplate.convertAndSend("/secured/simulation/set-vehicle-position", new DriverDTO(driver));
        return new ResponseEntity<>(new DriverDTO(driver), HttpStatus.OK);
    }

    @GetMapping(value = "/closest-stop/{id}", produces = "application/json")
    public ResponseEntity<PointDTO> getClosestTaxiStop(@PathVariable("id") Integer id) {
        Point closestStation = driverService.findClosestTaxiStop(id);
        return new ResponseEntity<>(new PointDTO(closestStation), HttpStatus.OK);
    }
}
