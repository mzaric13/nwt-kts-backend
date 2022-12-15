package nwt.kts.backend.service;

import nwt.kts.backend.dto.creation.AnsweredDriverDataCreationDTO;
import nwt.kts.backend.dto.creation.PasswordChangeCreationDTO;
import nwt.kts.backend.dto.creation.ProfilePictureCreationDTO;
import nwt.kts.backend.dto.returnDTO.AdminDTO;
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

    public User changePersonalInfo(AdminDTO userReturnDTO) {
        User user = userRepository.findUserByEmail(userReturnDTO.getEmail());
        return userService.updatePersonalUserInfo(user, userReturnDTO.getName(), userReturnDTO.getSurname(), userReturnDTO.getCity(), userReturnDTO.getPhoneNumber());
    }

    public List<Passenger> getAllPassengers() {
        return passengerRepository.findAll();
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Passenger changeBlockedStatusPassenger(Integer id) {
        Passenger passenger = passengerRepository.findPassengerById(id);
        passenger.setBlocked(!passenger.isBlocked());
        return passengerRepository.save(passenger);
    }

    public Driver changeBlockedStatusDriver(Integer id) {
        Driver driver = driverRepository.findDriverById(id);
        driver.setBlocked(!driver.isBlocked());
        return driverRepository.save(driver);
    }

    public User findAdminByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
}
