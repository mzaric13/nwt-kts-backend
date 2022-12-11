package nwt.kts.backend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import nwt.kts.backend.dto.creation.PassengerCreationDTO;
import nwt.kts.backend.dto.creation.PasswordChangeCreationDTO;
import nwt.kts.backend.dto.creation.ProfilePictureCreationDTO;
import nwt.kts.backend.dto.login.FacebookLoginData;
import nwt.kts.backend.dto.returnDTO.PassengerDTO;
import nwt.kts.backend.entity.Passenger;
import nwt.kts.backend.entity.Provider;
import nwt.kts.backend.entity.Role;
import nwt.kts.backend.entity.User;
import nwt.kts.backend.exceptions.InvalidUserDataException;
import nwt.kts.backend.exceptions.NonExistingEntityException;
import nwt.kts.backend.repository.PassengerRepository;
import nwt.kts.backend.repository.RoleRepository;
import nwt.kts.backend.repository.UserRepository;
import nwt.kts.backend.validation.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserValidator userValidator = new UserValidator();

    public Passenger createPassenger(PassengerCreationDTO passengerCreationDTO) throws MessagingException {
        userValidator.validateNewPassenger(passengerCreationDTO);
        Role role = roleRepository.findRoleByName("ROLE_PASSENGER");
        if (role == null) throw new InvalidUserDataException("Passenger role doesn't exist.");
        Passenger passenger = new Passenger(passengerCreationDTO.getEmail(), passengerCreationDTO.getPhoneNumber(), passwordEncoder.encode(passengerCreationDTO.getPassword()), passengerCreationDTO.getName(), passengerCreationDTO.getSurname(), passengerCreationDTO.getCity(), role, false, false, "default.jpg", Provider.LOCAL);
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

    public Passenger findPassengerById(int id) {
        return passengerRepository.findPassengersById(id);
    }

    public Passenger savePassenger(Passenger passenger) {
        return passengerRepository.save(passenger);
    }
    
    public Passenger createPassengerFacebookLogin(FacebookLoginData facebookLoginData, String email, String picture) {
        Passenger passenger = passengerRepository.findPassengerByEmail(email);
        if (passenger == null) {
            Role role = roleRepository.findRoleByName("ROLE_PASSENGER");
            if (role == null) throw new InvalidUserDataException("Passenger role doesn't exist.");
            passenger = new Passenger(email, facebookLoginData.getName(), facebookLoginData.getSurname(), role, picture, Provider.FACEBOOK);
            passenger = passengerRepository.save(passenger);
        }
        return passenger;
    }

    public Passenger createPassengerGoogleLogin(GoogleIdToken.Payload payload) {
        Passenger passenger = passengerRepository.findPassengerByEmail(payload.getEmail());
        if (passenger == null) {
            Role role = roleRepository.findRoleByName("ROLE_PASSENGER");
            if (role == null) throw new InvalidUserDataException("Passenger role doesn't exist.");
            String name = (String) payload.get("given_name");
            String surname = (String) payload.get("family_name");
            String picture = (String) payload.get("picture");
            passenger = new Passenger(payload.getEmail(), name, surname, role, picture, Provider.GOOGLE);
            passenger = passengerRepository.save(passenger);
        }
        return passenger;
    }

    public Passenger findPassengerByEmail(String email) {
        return passengerRepository.findPassengerByEmail(email);
    }
}
