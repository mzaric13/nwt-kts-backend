package nwt.kts.backend.controller;

import nwt.kts.backend.dto.creation.TempDriveDTO;
import nwt.kts.backend.dto.returnDTO.DriveDTO;
import nwt.kts.backend.dto.returnDTO.DriverDTO;
import nwt.kts.backend.entity.*;
import nwt.kts.backend.exceptions.DriverNotFoundException;
import nwt.kts.backend.exceptions.NotEnoughTokensException;
import nwt.kts.backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

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
    private RouteService routeService;

    @Autowired
    private TypeService typeService;

    @GetMapping("/get-drives")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDrives(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Page<Drive> drivePage = driveService.getDrives(page, size);
        return new ResponseEntity<>(createDrivesResponse(drivePage), HttpStatus.OK);
    }

    @GetMapping("/get-drives-for-driver")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Map<String, Object>> getDrivesForDriver(Principal principal, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Driver driver = driverService.findDriverByEmail(principal.getName());
        Page<Drive> drivePage = driveService.getDrivesByDriver(page, size, driver);
        return new ResponseEntity<>(createDrivesResponse(drivePage), HttpStatus.OK);
    }

    @GetMapping("/get-drives-for-passenger")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<Map<String, Object>> getDrivesForPassenger(Principal principal, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Passenger passenger = passengerService.findPassengerByEmail(principal.getName());
        Page<Drive> drivePage = driveService.getDrivesByPassenger(page, size, passenger);
        return new ResponseEntity<>(createDrivesResponse(drivePage), HttpStatus.OK);
    }

    @PostMapping("/create-temp-drive")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<Integer> createTempDrive(@RequestBody TempDriveDTO tempDriveDTO) {
        if (!passengerService.allPassengersExist(tempDriveDTO.getEmails()))
            throw new EntityNotFoundException("Not all passenger emails exist");
        Set<Passenger> passengers = tempDriveDTO.getEmails().stream()
                .map(email -> passengerService.findPassengerByEmail(email)).collect(Collectors.toSet());
        Type type = typeService.findTypeByName(tempDriveDTO.getTypeDTO().getName());
        TempDrive tempDrive = new TempDrive(tempDriveDTO, passengers, type);
        Route route = routeService.saveRoute(tempDrive.getRoute());
        tempDrive.setRoute(route);
        driveService.saveTempDrive(tempDrive);
        return new ResponseEntity<>(tempDrive.getId(), HttpStatus.CREATED);
    }

    @GetMapping("/send-confirmation-email/{tempDriveId}")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<Void> sendConfirmationEmail(Principal principal, @PathVariable Integer tempDriveId) throws MessagingException {
        TempDrive tempDrive = driveService.getTempDriveById(tempDriveId);
        driveService.sendConfirmationEmail(tempDrive);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/accept-drive-consent")
    public ResponseEntity<Void> acceptDriveConsent(Principal principal, @RequestParam("tempDriveId") Integer tempDriveId) {
        TempDrive tempDrive = driveService.getTempDriveById(tempDriveId);
        driveService.acceptDrive(tempDrive);
        if (driveService.allPassengersAcceptedDrive(tempDrive)) {
            if (driveService.passengersHaveTokens(tempDrive)) {
                Driver driver = driverService.selectDriverForDrive(tempDrive);
                if (driver == null) throw new DriverNotFoundException("There are no available drivers right now!");
                driveService.payDrive(tempDrive);
                driveService.createDrive(tempDrive, driver);
            } else {
                throw new NotEnoughTokensException("You don't have enough tokens to pay for the ride!");
            }
        }
        driveService.saveTempDrive(tempDrive);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/reject-drive-consent")
    public ResponseEntity<Void> rejectDriveConsent(Principal principal, @RequestParam("tempDriveId") Integer tempDriveId,
                                                   @RequestParam("passengerId") Integer passengerId) throws MessagingException {
        TempDrive tempDrive = driveService.getTempDriveById(tempDriveId);
        Passenger rejectPassenger = passengerService.findPassengerById(passengerId);
        driveService.rejectDrive(tempDrive, passengerId, rejectPassenger);
        return new ResponseEntity<>(HttpStatus.OK);
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

    @GetMapping(value = "/get-paid-drive", consumes = "application/json", produces = "application/json")
    public ResponseEntity<DriveDTO> getPaidDriveForDriver(@RequestBody DriverDTO driverDTO) {
        Driver driver = driverService.findDriverById(driverDTO.getId());
        Drive drive = driveService.getDriveForDriverByStatus(driver, Status.PAID);
        return new ResponseEntity<>(new DriveDTO(drive), HttpStatus.OK);
    }

    @GetMapping(value = "/get-started-drive", consumes = "application/json", produces = "application/json")
    public ResponseEntity<DriveDTO> getStartedDriveForDriver(@RequestBody DriverDTO driverDTO) {
        Driver driver = driverService.findDriverById(driverDTO.getId());
        Drive drive = driveService.getDriveForDriverByStatus(driver, Status.STARTED);
        return new ResponseEntity<>(new DriveDTO(drive), HttpStatus.OK);
    }

    @GetMapping(value = "/get-ended-drive", consumes = "application/json", produces = "application/json")
    public ResponseEntity<DriveDTO> getEndedDriveForDriver(@RequestBody DriverDTO driverDTO) {
        Driver driver = driverService.findDriverById(driverDTO.getId());
        Drive drive = driveService.getDriveForDriverByStatus(driver, Status.FINISHED);
        return new ResponseEntity<>(new DriveDTO(drive), HttpStatus.OK);
    }

    @PutMapping(value = "/start-drive", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<DriveDTO> startDrive(@RequestBody DriveDTO driveDTO) {
        Drive drive = driveService.startDrive(driveDTO);
        // TODO: socket call for passengers
        return new ResponseEntity<>(new DriveDTO(drive), HttpStatus.OK);
    }

    @PutMapping(value = "/end-drive", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<DriveDTO> endDrive(@RequestBody DriveDTO driveDTO) {
        Drive drive = driveService.endDrive(driveDTO);
        // TODO: socket call for map update and for passenger to rate drive
        return new ResponseEntity<>(new DriveDTO(drive), HttpStatus.OK);
    }
}
