package nwt.kts.backend.service;

import nwt.kts.backend.dto.creation.*;
import nwt.kts.backend.dto.returnDTO.DatesChartDTO;
import nwt.kts.backend.dto.returnDTO.PointDTO;
import nwt.kts.backend.entity.*;
import nwt.kts.backend.repository.*;
import nwt.kts.backend.validation.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

    @Autowired
    private PointRepository pointRepository;

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
        List<Drive> drives = driveRepository.findAllByStartDateAfterAndEndDateBeforeAndDriverOrderByStartDateAsc(datesChartDTO.getStartDate(), datesChartDTO.getEndDate(), driver);
        return createChartForDriver(drives);
    }

    public Driver changeStatus(Driver driver) {
        driver.setAvailable(!driver.isAvailable());
        return driverRepository.save(driver);
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
                new ChartObjectCreationDTO("Money earned per day", listMoneySpentOrEarnedPerDay));
    }

    private void updateHashtable(String date, Hashtable<String, Double> hashtable, Double updateValue) {
        if (hashtable.containsKey(date)) {
            hashtable.replace(date, hashtable.get(date), hashtable.get(date) + updateValue);
        } else {
            hashtable.put(date, updateValue);
        }
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

    public Driver findDriverById(Integer id) {
        return driverRepository.findDriverById(id);
    }

    public Driver updateDriverPosition(Integer id, PointDTO pointDTO) {
        Driver driver = driverRepository.findDriverById(id);
        driver.setLocation(new Point(pointDTO.getLatitude(), pointDTO.getLongitude()));
        return driverRepository.save(driver);
    }

    public Point findClosestTaxiStop(Integer id) {
        Driver driver = driverRepository.findDriverById(id);
        Page<Point> stations = pointRepository.findAll(PageRequest.of(0, 5, Sort.by("id")));
        Point station = null;
        double minDistance = Double.POSITIVE_INFINITY;
        for (Point point: stations.getContent()) {
            double distance = Math.pow(driver.getLocation().getLatitude() - point.getLatitude(), 2) + Math.pow(driver.getLocation().getLongitude() - point.getLongitude(), 2);
            distance = Math.sqrt(distance);
            if (distance < minDistance) {
                minDistance = distance;
                station = point;
            }
        }
        return station;
    }
}
