package nwt.kts.backend.service;

import nwt.kts.backend.dto.creation.*;
import nwt.kts.backend.dto.returnDTO.DatesChartDTO;
import nwt.kts.backend.entity.*;
import nwt.kts.backend.repository.DriveRepository;
import nwt.kts.backend.repository.DriverDataRepository;
import nwt.kts.backend.repository.DriverRepository;
import nwt.kts.backend.repository.UserRepository;
import nwt.kts.backend.validation.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.List;

@Service
@Transactional
public class DriverService {

    /**
     * Services
     */
    @Autowired
    private RoleService roleService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Repositories
     */

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private DriverDataRepository driverDataRepository;

    @Autowired
    private DriveRepository driveRepository;

    /**
     * Constants
     */
    private final String DRIVER_NAME = "ROLE_DRIVER";


    /**
     * Validators
     */
    private final UserValidator userValidator = new UserValidator();


    public Driver createDriver(DriverCreationDTO driverCreationDTO) {
        userValidator.validateNewUser(driverCreationDTO);
        vehicleService.validateNewVehicle(driverCreationDTO.getVehicleCreationDTO());
        Role role = roleService.findRoleByName(DRIVER_NAME);
        Type type = typeService.findTypeByName(driverCreationDTO.getVehicleCreationDTO().getType());
        return driverRepository.save(new Driver(driverCreationDTO.getEmail(), driverCreationDTO.getPhoneNumber(), passwordEncoder.encode(driverCreationDTO.getPassword()), driverCreationDTO.getName(), driverCreationDTO.getSurname(), driverCreationDTO.getCity(), driverCreationDTO, role, type, Provider.LOCAL));
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }
    
    public DriverData sendUpdateRequest(UpdatedUserDataCreationDTO updatedUserDataCreationDTO) {
        userValidator.validateUpdatedUserData(updatedUserDataCreationDTO);
        return driverDataRepository.save(new DriverData(updatedUserDataCreationDTO));
    }

    public Driver changePassword(PasswordChangeCreationDTO passwordChangeCreationDTO) {
        User user = userService.changePassword(passwordChangeCreationDTO);
        return driverRepository.findDriverByEmail(user.getEmail());
    }

    public Driver changeProfilePicture(ProfilePictureCreationDTO profilePictureCreationDTO) {
        User user = userService.changeProfilePicture(profilePictureCreationDTO);
        return driverRepository.findDriverByEmail(user.getEmail());
    }

    public Driver findDriverByEmail(String email) {
        return driverRepository.findDriverByEmail(email);
    }

    public DriverData findUnansweredDriverData(String email) {
        return driverDataRepository.getDriverDataByIsAnsweredAndEmail(false, email);
    }

    public ChartCreationDTO createDriverChart(Driver driver, DatesChartDTO datesChartDTO) {
        List<Drive> drives = driveRepository.findAllByStartDateAfterAndEndDateBeforeAndDriver(datesChartDTO.getStartDate(), datesChartDTO.getEndDate(), driver);
        return createChartForDriver(drives);
    }

    private ChartCreationDTO createChartForDriver(List<Drive> drives) {
        Hashtable<String, Double> drivesPerDay = new Hashtable<>();
        Hashtable<String, Double> drivenKilometersPerDay = new Hashtable<>();
        Hashtable<String, Double> moneySpentOrEarnedPerDay = new Hashtable<>();
        for (Drive drive :
                drives) {
            String date = drive.getStartDate().toString().split(" ")[0];
            updateHashtable(date, drivesPerDay, 1.0);
            updateHashtable(date, drivenKilometersPerDay, drive.getLength());
            updateHashtable(date, moneySpentOrEarnedPerDay, drive.getPrice());
        }
        return null;
        //return new ChartCreationDTO(drivesPerDay, drivenKilometersPerDay, moneySpentOrEarnedPerDay);
    }

    private void updateHashtable(String date, Hashtable<String, Double> hashtable, Double updateValue) {
        if (hashtable.containsKey(date)) {
            hashtable.replace(date, hashtable.get(date), hashtable.get(date) + updateValue);
        } else {
            hashtable.put(date, updateValue);
        }
    }
}
