package nwt.kts.backend.service;

import nwt.kts.backend.dto.returnDTO.DriveDTO;
import nwt.kts.backend.entity.*;
import nwt.kts.backend.exceptions.DriverNotOnLocationException;
import nwt.kts.backend.exceptions.NonExistingEntityException;
import nwt.kts.backend.repository.DriveRepository;
import nwt.kts.backend.repository.DriverRepository;
import nwt.kts.backend.repository.PassengerRepository;
import nwt.kts.backend.repository.TempDriveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.*;

@Service
public class DriveService {

    @Autowired
    private DriveRepository driveRepository;

    @Autowired
    private DriverService driverService;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private TempDriveRepository tempDriveRepository;

    @Autowired
    private EmailService emailService;

    public Page<Drive> getDrives(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return driveRepository.findAllByStatusOrderByStartDateAsc(Status.FINISHED, pageable);
    }

    public Page<Drive> getDrivesByDriver(int page, int size, Driver driver) {
        Pageable pageable = PageRequest.of(page, size);
        return driveRepository.findAllByDriverAndStatusOrderByStartDateAsc(driver, Status.FINISHED, pageable);
    }

    public Page<Drive> getDrivesByPassenger(int page, int size, Passenger passenger) {
        Pageable pageable = PageRequest.of(page, size);
        return driveRepository.findAllByPassengersContainsAndStatusOrderByStartDateAsc(passenger, Status.FINISHED, pageable);
    }

    public TempDrive saveTempDrive(TempDrive tempDrive) {
        return tempDriveRepository.save(tempDrive);
    }

    public TempDrive getTempDriveById(int id) {
        return tempDriveRepository.findTempDriveById(id);
    }

    public boolean passengersHaveTokens(TempDrive tempDrive) {
        double costPerPerson = tempDrive.getPrice() / tempDrive.getPassengers().size();
        double residueCost = 0;
        List<Passenger> sortedPassengers = sortPassengersByTokenAmount(tempDrive.getPassengers());
        if (sortedPassengers.get(0).getTokens() >= tempDrive.getPrice()) return true;
        for (Passenger passenger : sortedPassengers) {
            costPerPerson += residueCost;
            if (passenger.getTokens() >= costPerPerson) {
                residueCost = 0;
            }
            else {
                residueCost = costPerPerson - passenger.getTokens();
            }
        }
        return residueCost == 0;
    }

    private List<Passenger> sortPassengersByTokenAmount(Set<Passenger> passengers) {
        List<Passenger> sortedPassengers = new ArrayList<>(passengers);
        sortedPassengers.sort(Comparator.comparingDouble(Passenger::getTokens));
        return sortedPassengers;
    }

    public void rejectDrive(TempDrive tempDrive, Integer passengerId, Passenger rejectPassenger) throws MessagingException {
        for (Passenger passenger : tempDrive.getPassengers()) {
            if (!Objects.equals(passenger.getId(), passengerId)) {
                emailService.sendDriveRejectedEmail(tempDrive, passenger, rejectPassenger);
            }
        }
    }

    public void sendConfirmationEmail(TempDrive tempDrive) throws MessagingException {
        for (Passenger passenger : tempDrive.getPassengers()) {
            emailService.sendDriveConfirmationEmail(tempDrive, passenger);
        }
    }

    public void acceptDrive(TempDrive tempDrive) {
        tempDrive.addAcceptedPassenger();
    }

    public boolean allPassengersAcceptedDrive(TempDrive tempDrive) {
        return tempDrive.getNumAcceptedPassengers() == tempDrive.getPassengers().size();
    }

    public Drive createDrive(TempDrive tempDrive, Driver driver) {
        Drive drive = new Drive(tempDrive, driver);
        return driveRepository.save(drive);
    }

    public void payDrive(TempDrive tempDrive) {
        double costPerPerson = tempDrive.getPrice() / tempDrive.getPassengers().size();
        double residueCost = 0;
        List<Passenger> sortedPassengers = sortPassengersByTokenAmount(tempDrive.getPassengers());
        for (Passenger passenger : sortedPassengers) {
            costPerPerson += residueCost;
            if (passenger.getTokens() >= costPerPerson) {
                passenger.payDrive(costPerPerson);
                residueCost = 0;
            }
            else {
                residueCost = costPerPerson - passenger.getTokens();
                passenger.setTokens(0);
            }
        }
    }

    public Drive getDriveForDriverByStatus(Driver driver, Status status) {
        return driveRepository.findDriveByDriverAndStatus(driver, status);
    }

    public Drive startDrive(DriveDTO driveDTO) {
        Drive drive = driveRepository.findDriveById(driveDTO.getId());
        if (drive == null) throw new NonExistingEntityException("Drive is not found");
        if (checkDriverPositionToDrive(drive.getDriver(), drive.getRoute().getWaypoints().get(0))) throw new DriverNotOnLocationException("You can't start drive without being on location.");
        drive.setStatus(Status.STARTED);
        return driveRepository.save(drive);
    }

    public Drive endDrive(DriveDTO driveDTO) {
        Drive drive = driveRepository.findDriveById(driveDTO.getId());
        if (drive == null) throw new NonExistingEntityException("Drive is not found");
        Driver driver = drive.getDriver();
        if (checkDriverPositionToDrive(driver, drive.getRoute().getWaypoints().get(drive.getRoute().getWaypoints().size() - 1))) throw new DriverNotOnLocationException("You can't end drive without being on location.");
        if (!driver.isHasFutureDrive()) driverService.changeStatus(driver);
        drive.setStatus(Status.FINISHED);
        return driveRepository.save(drive);
    }

    private boolean checkDriverPositionToDrive(Driver driver, Point point) {
        return driver.getLocation().getLatitude() - point.getLatitude() < 10e-5 && driver.getLocation().getLongitude() - point.getLongitude() < 10e-5;
    }
}
