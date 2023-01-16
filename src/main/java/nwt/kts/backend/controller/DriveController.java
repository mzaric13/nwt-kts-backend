package nwt.kts.backend.controller;

import nwt.kts.backend.dto.creation.TempDriveDTO;
import nwt.kts.backend.dto.returnDTO.DriveDTO;
import nwt.kts.backend.entity.Drive;
import nwt.kts.backend.entity.Passenger;
import nwt.kts.backend.entity.Route;
import nwt.kts.backend.entity.TempDrive;
import nwt.kts.backend.service.DriveService;
import nwt.kts.backend.service.PassengerService;
import nwt.kts.backend.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value="/drives")
public class DriveController {

    @Autowired
    private DriveService driveService;

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private RouteService routeService;

    @GetMapping("/get-drives")
    public ResponseEntity<Map<String, Object>> getDrives(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Page<Drive> drivePage = driveService.getDrives(page, size);
        return new ResponseEntity<>(createDrivesResponse(drivePage), HttpStatus.OK);
    }

    @GetMapping("/get-drives-for-driver")
    public ResponseEntity<Map<String, Object>> getDrivesForDriver(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        // TODO: get driver from JWT
        Page<Drive> drivePage = driveService.getDrivesByDriver(page, size);
        return new ResponseEntity<>(createDrivesResponse(drivePage), HttpStatus.OK);
    }

    @GetMapping("/get-drives-for-passenger")
    public ResponseEntity<Map<String, Object>> getDrivesForPassenger(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        // TODO: get passenger from JWT
        Page<Drive> drivePage = driveService.getDrivesByPassenger(page, size);
        return new ResponseEntity<>(createDrivesResponse(drivePage), HttpStatus.OK);
    }

    @PostMapping("/create-temp-drive")
    public ResponseEntity<Void> createTempDrive(@RequestBody TempDriveDTO tempDriveDTO) {
        if (!passengerService.allPassengersExist(tempDriveDTO.getEmails()))
            throw new EntityNotFoundException("Not all passenger emails exist");
        Set<Passenger> passengers = tempDriveDTO.getEmails().stream()
                .map(email -> passengerService.findPassengerByEmail(email)).collect(Collectors.toSet());
        TempDrive tempDrive = new TempDrive(tempDriveDTO, passengers);
        Route route = routeService.saveRoute(tempDrive.getRoute());
        tempDrive.setRoute(route);
        driveService.saveTempDrive(tempDrive);
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
