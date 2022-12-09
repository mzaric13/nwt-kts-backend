package nwt.kts.backend.controller;

import nwt.kts.backend.dto.creation.PassengerCreationDTO;
import nwt.kts.backend.dto.creation.PasswordChangeCreationDTO;
import nwt.kts.backend.dto.creation.ProfilePictureCreationDTO;
import nwt.kts.backend.dto.returnDTO.PassengerDTO;
import nwt.kts.backend.entity.Passenger;
import nwt.kts.backend.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.security.Principal;

@RestController
@RequestMapping(value = "/passengers")
public class PassengerController {

    @Autowired
    private PassengerService passengerService;

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<PassengerDTO> registerPassenger(@RequestBody PassengerCreationDTO passengerCreationDTO) throws MessagingException {
        Passenger passenger = passengerService.createPassenger(passengerCreationDTO);
        return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.CREATED);
    }

    @GetMapping(value = "/activate-account/{id}")
    public ResponseEntity<PassengerDTO> activateAccount(@PathVariable Integer id) {
        Passenger passenger = passengerService.activateAccount(id);
        return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
    }

    @PutMapping(value = "/change-password")
    public ResponseEntity<PassengerDTO> changePassword(@RequestBody PasswordChangeCreationDTO passwordChangeCreationDTO){
        Passenger passenger = passengerService.changePassword(passwordChangeCreationDTO);
        return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
    }

    @PutMapping(value = "/change-profile-picture")
    public ResponseEntity<PassengerDTO> changeProfilePicture(@RequestBody ProfilePictureCreationDTO profilePictureCreationDTO){
        Passenger passenger = passengerService.changeProfilePicture(profilePictureCreationDTO);
        return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
    }

    @PutMapping(value = "/update-personal-info")
    public ResponseEntity<PassengerDTO> changePersonalInfo(@RequestBody PassengerDTO passengerDTO){
        Passenger passenger = passengerService.changePersonalInfo(passengerDTO);
        return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
    }

    @GetMapping(value = "/get-logged")
    public ResponseEntity<PassengerDTO> getLoggedPassenger(Principal principal) {
        Passenger passenger = passengerService.findPassengerByEmail(principal.getName());
        return new ResponseEntity<>(new PassengerDTO(passenger), HttpStatus.OK);
    }
}
