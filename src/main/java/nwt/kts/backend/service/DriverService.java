package nwt.kts.backend.service;

import nwt.kts.backend.dto.creation.DriverCreationDTO;
import nwt.kts.backend.dto.creation.PasswordChangeCreationDTO;
import nwt.kts.backend.dto.creation.ProfilePictureCreationDTO;
import nwt.kts.backend.dto.creation.UpdatedUserDataCreationDTO;
import nwt.kts.backend.entity.*;
import nwt.kts.backend.repository.DriveRepository;
import nwt.kts.backend.repository.DriverDataRepository;
import nwt.kts.backend.repository.DriverRepository;
import nwt.kts.backend.repository.UserRepository;
import nwt.kts.backend.validation.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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

    @Scheduled(fixedRate = 60000)
    @Transactional(readOnly = false)
    public void checkDriversWorkTime() {
        List<Driver> drivers = driverRepository.findAll();
        Date now = new Date();
        Timestamp timestampNow = new Timestamp(now.getTime());
        for (Driver driver: drivers) {
            if (driver.getTimeOfLogin() != null) {
                long difference = timestampNow.getTime() - driver.getTimeOfLogin().getTime();
                long diffHours = difference / (60 * 60 * 1000);
                if (diffHours >= 8) {
                    driver.setAvailable(false);
                    driver.setTimeOfLogin(null);
                    driverRepository.save(driver);
                }
            }
        }
    }

    @Transactional
    public void setDriverLoginTime(String email) {
        Driver driver = driverRepository.findDriverByEmail(email);
        Date now = new Date();
        Timestamp timestampNow =new Timestamp(now.getTime());
        driver.setTimeOfLogin(timestampNow);
        driver.setAvailable(true);
        driverRepository.save(driver);
    }

    public Driver selectDriverForDrive(TempDrive tempDrive) {
        Driver closestDriver = null;
        closestDriver = findFromAvailableDrivers(tempDrive);
        if (closestDriver == null) {
            closestDriver = findFromDriversWithDrive(tempDrive);
        }
        return closestDriver;
    }

    private Driver findFromAvailableDrivers(TempDrive tempDrive) {
        List<Driver> availableDrivers = driverRepository.findDriversByIsAvailable(true);
        if (availableDrivers.size() == 0) return null;
        else {
            Driver closestDriver = null;
            double minDistance = Double.POSITIVE_INFINITY;
            ArrayList<Point> waypoints = new ArrayList<>(tempDrive.getRoute().getWaypoints());
            for (Driver driver: availableDrivers) {
                if (driver.getVehicle().getType().getId().equals(tempDrive.getVehicleType().getId())) {
                    double distance = Math.pow(driver.getLocation().getLatitude() - waypoints.get(waypoints.size() - 1).getLatitude(), 2) + Math.pow(driver.getLocation().getLongitude() - waypoints.get(waypoints.size() - 1).getLongitude(), 2);
                    distance = Math.sqrt(distance);
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestDriver = driver;
                    }
                }
            }
            return closestDriver;
        }
    }

    private Driver findFromDriversWithDrive(TempDrive tempDrive) {
        List<Driver> nonAvailableDrivers = driverRepository.findDriversByIsAvailable(false);
        Driver closestDriver = null;
        double minDistance = Double.POSITIVE_INFINITY;
        for (Driver driver: nonAvailableDrivers) {
            if (driver.getTimeOfLogin() != null && !driver.isHasFutureDrive() && driver.getVehicle().getType().getId().equals(tempDrive.getVehicleType().getId())) {
                Drive currentDrive = driveRepository.findDriveByDriverAndStatus(driver, Status.STARTED);
                ArrayList<Point> waypoints = new ArrayList<>(currentDrive.getRoute().getWaypoints());
                double distance = Math.pow(driver.getLocation().getLatitude() - waypoints.get(0).getLatitude(), 2) + Math.pow(driver.getLocation().getLongitude() - waypoints.get(0).getLongitude(), 2);
                distance = Math.sqrt(distance);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestDriver = driver;
                }
            }
        }
        if (closestDriver != null) {
            closestDriver.setHasFutureDrive(true);
            return driverRepository.save(closestDriver);
        }
        return null;
    }
}
