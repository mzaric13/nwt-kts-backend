package nwt.kts.backend.controller;

import nwt.kts.backend.dto.returnDTO.PassengerDTO;
import nwt.kts.backend.dto.returnDTO.PasswordResetDTO;
import nwt.kts.backend.dto.returnDTO.UserDTO;
import nwt.kts.backend.dto.returnDTO.UserPassResetDTO;
import nwt.kts.backend.entity.Passenger;
import nwt.kts.backend.entity.User;
import nwt.kts.backend.service.EmailService;
import nwt.kts.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @GetMapping(value = "/get-user/{emailAddress}")
    public ResponseEntity<UserPassResetDTO> checkIfUserExist(@PathVariable(value = "emailAddress") String emailAddress) throws MessagingException {
        User user = userService.findUserByEmail(emailAddress);
        if (user == null) return new ResponseEntity<>(new UserPassResetDTO("User with specified username doesn't exist.", false), HttpStatus.OK);
        else {
            emailService.sendPasswordResetEmail(user);
            return new ResponseEntity<>(new UserPassResetDTO("You will receive email with link for password reset.", true), HttpStatus.OK);
        }
    }

    @PutMapping(value = "/reset-password/{emailAddress}")
    public ResponseEntity<UserDTO> resetPassword(@RequestBody PasswordResetDTO passwordResetDTO, @PathVariable(value = "emailAddress") String emailAddress) {
        User user = userService.resetPassword(passwordResetDTO, emailAddress);
        return new ResponseEntity<>(new UserDTO(user), HttpStatus.OK);
    }
}
