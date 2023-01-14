package nwt.kts.backend.controller;

import nwt.kts.backend.dto.returnDTO.DriveDTO;
import nwt.kts.backend.entity.Drive;
import nwt.kts.backend.entity.Driver;
import nwt.kts.backend.entity.Passenger;
import nwt.kts.backend.service.DriveService;
import nwt.kts.backend.service.DriverService;
import nwt.kts.backend.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="/drives")
public class DriveController {

    @Autowired
    private DriveService driveService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private PassengerService passengerService;

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
