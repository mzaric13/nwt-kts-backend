package nwt.kts.backend.controller;

import nwt.kts.backend.dto.creation.TempDriveDTO;
import nwt.kts.backend.dto.returnDTO.DriveDTO;
import nwt.kts.backend.entity.*;
import nwt.kts.backend.service.DriveService;
import nwt.kts.backend.service.DriverService;
import nwt.kts.backend.service.PassengerService;
import nwt.kts.backend.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.security.Principal;

@RestController
@RequestMapping(value="/drives")
public class DriveController {

    @Autowired
    private DriveService driveService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private TypeService typeService;

    @GetMapping("/get-drives")
    public ResponseEntity<Map<String, Object>> getDrives(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Page<Drive> drivePage = driveService.getDrives(page, size);
        return new ResponseEntity<>(createDrivesResponse(drivePage), HttpStatus.OK);
    }

    @GetMapping("/get-drives-for-driver")
    public ResponseEntity<Map<String, Object>> getDrivesForDriver(Principal principal, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Driver driver = driverService.findDriverByEmail(principal.getName());
        Page<Drive> drivePage = driveService.getDrivesByDriver(page, size, driver);
        return new ResponseEntity<>(createDrivesResponse(drivePage), HttpStatus.OK);
    }

    @GetMapping("/get-drives-for-passenger")
    public ResponseEntity<Map<String, Object>> getDrivesForPassenger(Principal principal, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Passenger passenger = passengerService.findPassengerByEmail(principal.getName());
        Page<Drive> drivePage = driveService.getDrivesByPassenger(page, size, passenger);
        return new ResponseEntity<>(createDrivesResponse(drivePage), HttpStatus.OK);
    }

    @PostMapping("/create-temp-drive")
    public ResponseEntity<Void> createTempDrive(@RequestBody TempDriveDTO tempDriveDTO) {
        if (!passengerService.allPassengersExist(tempDriveDTO.getEmails()))
            throw new EntityNotFoundException("Not all passenger emails exist");
        Set<Passenger> passengers = tempDriveDTO.getEmails().stream()
                .map(email -> passengerService.findPassengerByEmail(email)).collect(Collectors.toSet());
        Type type = typeService.findTypeByName(tempDriveDTO.getTypeDTO().getName());
        TempDrive tempDrive = new TempDrive(tempDriveDTO, passengers, type);
        tempDrive = driveService.saveTempDrive(tempDrive);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private Map<String, Object> createDrivesResponse(Page<Drive> drivePage) {
        Map<String, Object> returnValue = new HashMap<>();
        List<DriveDTO> driveDTOS = new ArrayList<>();
        for (Drive drive: drivePage.getContent()) {
            driveDTOS.add(new DriveDTO(drive));
        }
        returnValue.put("drives", driveDTOS);
        returnValue.put("totalItems", drivePage.getTotalElements());
        returnValue.put("totalPages", drivePage.getTotalPages());
        return returnValue;
    }


}
