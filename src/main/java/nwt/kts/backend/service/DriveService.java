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

    public boolean checkPassengersTokenAvailability(Drive drive) {
        double costPerPersonNotRounded = drive.getPrice() / drive.getPassengers().size();
        int i = 0;
        int residueCost = 0;
        List<Passenger> sortedPassengers = sortPassengersByTokenAmount(drive.getPassengers());
        for (Passenger passenger : sortedPassengers) {
            int costPerPerson = (int) (i % 2 == 0 ? Math.ceil(costPerPersonNotRounded) : Math.floor(costPerPersonNotRounded));
            costPerPerson += residueCost;
            if (passenger.getTokens() >= costPerPerson) {
                passenger.payDrive(costPerPerson);
                residueCost = 0;
            }
            else {
                residueCost = costPerPerson - passenger.getTokens();
                passenger.setTokens(0);
            }
            i++;
        }
        return residueCost == 0;
    }

    private List<Passenger> sortPassengersByTokenAmount(Set<Passenger> passengers) {
        List<Passenger> sortedPassengers = new ArrayList<>(passengers);
        sortedPassengers.sort(Comparator.comparingInt(Passenger::getTokens));
        return sortedPassengers;
    }
}
