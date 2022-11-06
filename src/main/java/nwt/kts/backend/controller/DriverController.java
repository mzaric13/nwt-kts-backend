package nwt.kts.backend.controller;

import nwt.kts.backend.dto.creation.DriverCreationDTO;
import nwt.kts.backend.dto.returnDTO.DriverReturnDTO;
import nwt.kts.backend.entity.Driver;
import nwt.kts.backend.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value="/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @PostMapping(value="/register")
    public ResponseEntity<DriverReturnDTO> registerDriver(@RequestBody DriverCreationDTO driverCreationDTO) {
        Driver driver = driverService.createDriver(driverCreationDTO);
        return new ResponseEntity<>(new DriverReturnDTO(driver), HttpStatus.CREATED);
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<DriverReturnDTO>> getAllDrivers() {
        List<Driver> drivers = driverService.getAllDrivers();
        List<DriverReturnDTO> driverReturnDTOS = drivers.stream().map(DriverReturnDTO::new).collect(Collectors.toList());
        return new ResponseEntity<>(driverReturnDTOS, HttpStatus.OK);
    }
}
