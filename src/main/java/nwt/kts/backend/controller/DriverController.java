package nwt.kts.backend.controller;

import nwt.kts.backend.dto.creation.DriverCreationDTO;
import nwt.kts.backend.dto.creation.PasswordChangeCreationDTO;
import nwt.kts.backend.dto.creation.ProfilePictureCreationDTO;
import nwt.kts.backend.dto.creation.UpdatedUserDataCreationDTO;
import nwt.kts.backend.dto.returnDTO.DriverDataReturnDTO;
import nwt.kts.backend.dto.returnDTO.DriverReturnDTO;
import nwt.kts.backend.entity.Driver;
import nwt.kts.backend.entity.DriverData;
import nwt.kts.backend.service.DriverService;
import nwt.kts.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @Autowired
    private UserService userService;

    @PostMapping(value="/register")
    public ResponseEntity<DriverReturnDTO> registerDriver(@RequestBody DriverCreationDTO driverCreationDTO) {
        Driver driver = driverService.createDriver(driverCreationDTO);
        return new ResponseEntity<>(new DriverReturnDTO(driver), HttpStatus.CREATED);
    }

    @PostMapping(value="/send-update-request")
    public ResponseEntity<DriverDataReturnDTO> sendUpdateRequest(@RequestBody UpdatedUserDataCreationDTO updatedUserDataCreationDTO){
        DriverData driverData = driverService.sendUpdateRequest(updatedUserDataCreationDTO);
        return new ResponseEntity<>(new DriverDataReturnDTO(driverData), HttpStatus.CREATED);
    }

    @PutMapping(value="/change-password")
    public ResponseEntity<DriverReturnDTO> changePassword(@RequestBody PasswordChangeCreationDTO passwordChangeCreationDTO){
        Driver driver = driverService.changePassword(passwordChangeCreationDTO);
        return new ResponseEntity<>(new DriverReturnDTO(driver), HttpStatus.OK);
    }

    @PutMapping(value="/change-profile-picture")
    public ResponseEntity<DriverReturnDTO> changeProfilePicture(@RequestBody ProfilePictureCreationDTO profilePictureCreationDTO){
        Driver driver = driverService.changeProfilePicture(profilePictureCreationDTO);
        return new ResponseEntity<>(new DriverReturnDTO(driver), HttpStatus.OK);
    }
}
