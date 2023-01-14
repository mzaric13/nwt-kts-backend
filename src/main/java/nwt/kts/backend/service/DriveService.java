package nwt.kts.backend.service;

import nwt.kts.backend.entity.Drive;
import nwt.kts.backend.entity.Driver;
import nwt.kts.backend.entity.Passenger;
import nwt.kts.backend.entity.Status;
import nwt.kts.backend.repository.DriveRepository;
import nwt.kts.backend.repository.DriverRepository;
import nwt.kts.backend.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriveService {

    @Autowired
    private DriveRepository driveRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    public Page<Drive> getDrives(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return driveRepository.findAllByStatus(Status.FINISHED, pageable);
    }

    public Page<Drive> getDrivesByDriver(int page, int size, Driver driver) {
        Pageable pageable = PageRequest.of(page, size);
        return driveRepository.findAllByDriverAndStatus(driver, Status.FINISHED, pageable);
    }

    public Page<Drive> getDrivesByPassenger(int page, int size, Passenger passenger) {
        Pageable pageable = PageRequest.of(page, size);
        return driveRepository.findAllByPassengersContainsAndStatus(passenger, Status.FINISHED, pageable);
    }
}
