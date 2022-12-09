package nwt.kts.backend.controller;

import nwt.kts.backend.dto.creation.DriverCreationDTO;
import nwt.kts.backend.dto.creation.PasswordChangeCreationDTO;
import nwt.kts.backend.dto.creation.ProfilePictureCreationDTO;
import nwt.kts.backend.dto.creation.UpdatedUserDataCreationDTO;
import nwt.kts.backend.dto.returnDTO.DriverDataReturnDTO;
import nwt.kts.backend.dto.returnDTO.DriverDTO;
import nwt.kts.backend.dto.returnDTO.PassengerDTO;
import nwt.kts.backend.entity.Driver;
import nwt.kts.backend.entity.DriverData;
import nwt.kts.backend.service.DriverService;
import nwt.kts.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
    public ResponseEntity<DriverDataReturnDTO> sendUpdateRequest(@RequestBody UpdatedUserDataCreationDTO updatedUserDataCreationDTO){
        DriverData driverData = driverService.sendUpdateRequest(updatedUserDataCreationDTO);
        return new ResponseEntity<>(new DriverDataReturnDTO(driverData), HttpStatus.CREATED);
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
    public ResponseEntity<DriverDTO> getLoggedPassenger(Principal principal) {
        Driver driver = driverService.findDriverByEmail(principal.getName());
        return new ResponseEntity<>(new DriverDTO(driver), HttpStatus.OK);
    }
}
