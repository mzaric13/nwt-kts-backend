package nwt.kts.backend.service;

import nwt.kts.backend.dto.creation.*;
import nwt.kts.backend.dto.returnDTO.AdminDTO;
import nwt.kts.backend.dto.returnDTO.DatesChartDTO;
import nwt.kts.backend.entity.*;
import nwt.kts.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

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

    @Autowired
    private DriveRepository driveRepository;


    public Page<DriverData> getUnansweredDriverData(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return driverDataRepository.getAllByIsAnswered(false, pageable);
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

    public Passenger changeBlockedStatusPassenger(UserIdDTO userIdDTO) {
        Passenger passenger = passengerRepository.findPassengerById(userIdDTO.getId());
        //TODO DODAJ SLANJE PORUKE NA LIVE CHAT (poruka : userIdDTO.getReasoning())
        passenger.setBlocked(!passenger.isBlocked());
        return passengerRepository.save(passenger);
    }

    public Driver changeBlockedStatusDriver(UserIdDTO userIdDTO) {
        Driver driver = driverRepository.findDriverById(userIdDTO.getId());
        //TODO DODAJ SLANJE PORUKE NA LIVE CHAT (poruka : userIdDTO.getReasoning())
        driver.setBlocked(!driver.isBlocked());
        return driverRepository.save(driver);
    }

    public User findAdminByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public ChartCreationDTO createAdminChart(DatesChartDTO datesChartDTO) {
        List<Drive> drives = driveRepository.findAllByStartDateAfterAndEndDateBeforeOrderByStartDateAsc(datesChartDTO.getStartDate(), datesChartDTO.getEndDate());
        return createChartForAdmin(drives);
    }

    private ChartCreationDTO createChartForAdmin(List<Drive> drives) {
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
                new ChartObjectCreationDTO("Money earned/spent per day", listMoneySpentOrEarnedPerDay));
    }

    private void updateHashtable(String date, Hashtable<String, Double> hashtable, Double updateValue) {
        if (hashtable.containsKey(date)) {
            hashtable.replace(date, hashtable.get(date), hashtable.get(date) + updateValue);
        } else {
            hashtable.put(date, updateValue);
        }
    }

}
