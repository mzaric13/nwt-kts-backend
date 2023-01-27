package nwt.kts.backend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import nwt.kts.backend.dto.creation.*;
import nwt.kts.backend.dto.login.FacebookLoginData;
import nwt.kts.backend.dto.returnDTO.DatesChartDTO;
import nwt.kts.backend.dto.returnDTO.PassengerDTO;
import nwt.kts.backend.entity.*;
import nwt.kts.backend.exceptions.InvalidUserDataException;
import nwt.kts.backend.exceptions.NonExistingEntityException;
import nwt.kts.backend.repository.DriveRepository;
import nwt.kts.backend.repository.PassengerRepository;
import nwt.kts.backend.repository.RoleRepository;
import nwt.kts.backend.repository.UserRepository;
import nwt.kts.backend.validation.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.*;

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
    private DriveRepository driveRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserValidator userValidator = new UserValidator();

    public List<Passenger> getAllActivatedPassengers() {
        return passengerRepository.findAllByActivatedTrue();
    }

    public Passenger createPassenger(PassengerCreationDTO passengerCreationDTO) throws MessagingException {
        userValidator.validateNewPassenger(passengerCreationDTO);
        Role role = roleRepository.findRoleByName("ROLE_PASSENGER");
        if (role == null) throw new InvalidUserDataException("Passenger role doesn't exist.");
        Passenger passenger = new Passenger(passengerCreationDTO.getEmail(), passengerCreationDTO.getPhoneNumber(), passwordEncoder.encode(passengerCreationDTO.getPassword()), passengerCreationDTO.getName(), passengerCreationDTO.getSurname(), passengerCreationDTO.getCity(), role, false, false, "default.jpg", Provider.LOCAL);
        passenger = passengerRepository.save(passenger);
        emailService.sendActivationEmail(passenger);
        return passenger;
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
        return passengerRepository.findPassengerById(id);
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

    public Passenger addTokens(Passenger passenger, double tokensToAdd) {
        double currentTokens = passenger.getTokens();
        passenger.setTokens(currentTokens + tokensToAdd);
        return passengerRepository.save(passenger);
    }

    public boolean allPassengersExist(Set<String> emails) {
        for (String email : emails) {
            if (passengerRepository.findPassengerByEmail(email) == null) {
                return false;
            }
        }
        return true;
    }

    public ChartCreationDTO createPassengerChart(Passenger passenger, DatesChartDTO datesChartDTO) {
        List<Drive> drives = driveRepository.findAllByStartDateAfterAndEndDateBeforeAndPassengersContainsOrderByStartDateAsc(datesChartDTO.getStartDate(), datesChartDTO.getEndDate(), passenger);
        return createChartForPassenger(drives);
    }

    private ChartCreationDTO createChartForPassenger(List<Drive> drives) {
        Hashtable<String, Double> drivesPerDay = new Hashtable<>();
        Hashtable<String, Double> drivenKilometersPerDay = new Hashtable<>();
        Hashtable<String, Double> moneySpentOrEarnedPerDay = new Hashtable<>();

        for (Drive drive :
                drives) {
            String date = drive.getStartDate().toString().split(" ")[0];
            updateHashtable(date, drivesPerDay, 1.0);
            updateHashtable(date, drivenKilometersPerDay, drive.getLength());
            updateHashtable(date, moneySpentOrEarnedPerDay, drive.getPrice() / drive.getPassengers().size());
        }

        List<SeriesObjectCreationDTO> listDrivesPerDay = new ArrayList<>();
        List<SeriesObjectCreationDTO> listDrivenKilometersPerDay = new ArrayList<>();
        List<SeriesObjectCreationDTO> listMoneySpentOrEarnedPerDay = new ArrayList<>();

        Set<String> setOfKeys = drivesPerDay.keySet();

        for (String key : setOfKeys) {
            listDrivesPerDay.add(new SeriesObjectCreationDTO(key, drivesPerDay.get(key)));
            listDrivenKilometersPerDay.add(new SeriesObjectCreationDTO(key, drivenKilometersPerDay.get(key)));
            listMoneySpentOrEarnedPerDay.add(new SeriesObjectCreationDTO(key, moneySpentOrEarnedPerDay.get(key)));
        }

        return new ChartCreationDTO(new ChartObjectCreationDTO("Drives per day", listDrivesPerDay),
                new ChartObjectCreationDTO("Driven kilometers per day", listDrivenKilometersPerDay),
                new ChartObjectCreationDTO("Money spent per day", listMoneySpentOrEarnedPerDay));
    }

    private void updateHashtable(String date, Hashtable<String, Double> hashtable, Double updateValue) {
        if (hashtable.containsKey(date)) {
            hashtable.replace(date, hashtable.get(date), hashtable.get(date) + updateValue);
        } else {
            hashtable.put(date, updateValue);
        }
    }
}
