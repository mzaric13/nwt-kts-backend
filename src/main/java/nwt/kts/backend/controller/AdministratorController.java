package nwt.kts.backend.controller;

import nwt.kts.backend.dto.creation.AnsweredDriverDataCreationDTO;
import nwt.kts.backend.dto.creation.PasswordChangeCreationDTO;
import nwt.kts.backend.dto.creation.ProfilePictureCreationDTO;
import nwt.kts.backend.dto.returnDTO.DriverDataReturnDTO;
import nwt.kts.backend.dto.returnDTO.PassengerDTO;
import nwt.kts.backend.dto.returnDTO.UserReturnDTO;
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
    public ResponseEntity<UserReturnDTO> changePassword(@RequestBody PasswordChangeCreationDTO passwordChangeCreationDTO){
        User administrator = administratorService.changePassword(passwordChangeCreationDTO);
        return new ResponseEntity<>(new UserReturnDTO(administrator), HttpStatus.OK);
    }

    @PutMapping(value = "/change-profile-picture")
    public ResponseEntity<UserReturnDTO> changeProfilePicture(@RequestBody ProfilePictureCreationDTO profilePictureCreationDTO){
        User user = administratorService.changeProfilePicture(profilePictureCreationDTO);
        return new ResponseEntity<>(new UserReturnDTO(user), HttpStatus.OK);
    }

    @PutMapping(value = "/update-personal-info")
    public ResponseEntity<UserReturnDTO> changePersonalInfo(@RequestBody UserReturnDTO userReturnDTO){
        User user = administratorService.changePersonalInfo(userReturnDTO);
        return new ResponseEntity<>(new UserReturnDTO(user), HttpStatus.OK);
    }
}
