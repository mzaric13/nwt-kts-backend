package nwt.kts.backend.service;

import nwt.kts.backend.dto.creation.PassengerCreationDTO;
import nwt.kts.backend.dto.creation.PasswordChangeCreationDTO;
import nwt.kts.backend.dto.creation.ProfilePictureCreationDTO;
import nwt.kts.backend.dto.returnDTO.PassengerDTO;
import nwt.kts.backend.entity.Passenger;
import nwt.kts.backend.entity.Role;
import nwt.kts.backend.entity.User;
import nwt.kts.backend.exceptions.InvalidUserDataException;
import nwt.kts.backend.exceptions.NonExistingEntityException;
import nwt.kts.backend.repository.PassengerRepository;
import nwt.kts.backend.repository.RoleRepository;
import nwt.kts.backend.repository.UserRepository;
import nwt.kts.backend.validation.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.Optional;

@Service
public class PassengerService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    private final UserValidator userValidator = new UserValidator();

    public Passenger createPassenger(PassengerCreationDTO passengerCreationDTO) throws MessagingException {
        userValidator.validateNewPassenger(passengerCreationDTO);
        Role role = roleRepository.findRoleByName("passenger");
        if (role == null) throw new InvalidUserDataException("Passenger role doesn't exist.");
        Passenger passenger = new Passenger(passengerCreationDTO.getEmail(), passengerCreationDTO.getPhoneNumber(), passengerCreationDTO.getPassword(), passengerCreationDTO.getName(), passengerCreationDTO.getSurname(), passengerCreationDTO.getCity(), role, false, false, "default.jpg");
        passenger = passengerRepository.save(passenger);
        emailService.sendActivationEmail(passenger);
        return passengerRepository.save(passenger);
    }

    public Passenger activateAccount(Integer id) {
        Optional<Passenger> passenger = passengerRepository.findById(id);
        if (passenger.isPresent()) {
            Passenger pass = passenger.get();
            pass.setActivated(true);
            return passengerRepository.save(pass);
        } else throw new NonExistingEntityException("Passenger with given id doesn't exist.");
    }

    public Passenger changePassword(PasswordChangeCreationDTO passwordChangeCreationDTO) {
        User user = userService.changePassword(passwordChangeCreationDTO);
        return passengerRepository.findPassengerByEmail(user.getEmail());
    }

    public Passenger changeProfilePicture(ProfilePictureCreationDTO profilePictureCreationDTO) {
        User user = userService.changeProfilePicture(profilePictureCreationDTO);
        return passengerRepository.findPassengerByEmail(user.getEmail());
    }

    public Passenger changePersonalInfo(PassengerDTO passengerDTO) {
        User user = userRepository.findUserByEmail(passengerDTO.getEmail());
        User updatedUser = userService.updatePersonalUserInfo(user, passengerDTO.getName(), passengerDTO.getSurname(), passengerDTO.getCity(), passengerDTO.getPhoneNumber());
        return passengerRepository.findPassengerByEmail(updatedUser.getEmail());
    }

}
