package nwt.kts.backend.service;

import nwt.kts.backend.dto.creation.AnsweredDriverDataCreationDTO;
import nwt.kts.backend.dto.creation.PasswordChangeCreationDTO;
import nwt.kts.backend.dto.creation.ProfilePictureCreationDTO;
import nwt.kts.backend.dto.returnDTO.UserReturnDTO;
import nwt.kts.backend.entity.Driver;
import nwt.kts.backend.entity.DriverData;
import nwt.kts.backend.entity.Passenger;
import nwt.kts.backend.entity.User;
import nwt.kts.backend.repository.DriverDataRepository;
import nwt.kts.backend.repository.DriverRepository;
import nwt.kts.backend.repository.PassengerRepository;
import nwt.kts.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdministratorService {

    /**
     * Services
     */
    @Autowired
    private DriverService driverService;

    @Autowired
    private UserService userService;

    /**
     * Repositories
     */
    @Autowired
    private DriverDataRepository driverDataRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private DriverRepository driverRepository;


    public List<DriverData> getUnansweredDriverData() {
        return driverDataRepository.getAllByIsAnswered(false);
    }

    public DriverData answerDriverDataChange(AnsweredDriverDataCreationDTO answeredDriverDataCreationDTO) {

        DriverData answeredDriverDataChange = driverDataRepository.getDriverDataById(answeredDriverDataCreationDTO.getDriverDataId());
        User user = userRepository.findUserByEmail(answeredDriverDataChange.getEmail());

        if (answeredDriverDataCreationDTO.isApproved()) {
            userService.updatePersonalUserInfo(user, answeredDriverDataChange.getName(), answeredDriverDataChange.getSurname(),
                    answeredDriverDataChange.getCity(), answeredDriverDataChange.getPhoneNumber());
        }

        answeredDriverDataChange.setAnswered(true);
        return driverDataRepository.save(answeredDriverDataChange);
    }

    public User changePassword(PasswordChangeCreationDTO passwordChangeCreationDTO) {
        User user = userService.changePassword(passwordChangeCreationDTO);
        return userRepository.findUserByEmail(user.getEmail());
    }

    public User changeProfilePicture(ProfilePictureCreationDTO profilePictureCreationDTO) {
        User user = userService.changeProfilePicture(profilePictureCreationDTO);
        return userRepository.findUserByEmail(user.getEmail());
    }

    public User changePersonalInfo(UserReturnDTO userReturnDTO) {
        User user = userRepository.findUserByEmail(userReturnDTO.getEmail());
        return userService.updatePersonalUserInfo(user, userReturnDTO.getName(), userReturnDTO.getSurname(), userReturnDTO.getCity(), userReturnDTO.getPhoneNumber());
    }

    public List<Passenger> getAllNotBlockedPassengers() {
        return passengerRepository.findPassengersByIsBlocked(false);
    }

    public List<Driver> getAllNotBlockedDrivers() {
        return driverRepository.findDriversByIsBlocked(false);
    }

    public Passenger blockPassenger(String email) {
        Passenger passenger = passengerRepository.findPassengerByEmail(email);
        passenger.setBlocked(true);
        return passengerRepository.save(passenger);
    }

    public Driver blockDriver(String email) {
        Driver driver = driverRepository.findDriverByEmail(email);
        driver.setBlocked(true);
        return driverRepository.save(driver);
    }
}
