package nwt.kts.backend.service;

import nwt.kts.backend.entity.*;
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
    private DriverRepository driverRepository;

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
}
