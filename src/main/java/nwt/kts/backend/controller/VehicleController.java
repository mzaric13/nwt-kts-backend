package nwt.kts.backend.controller;

import nwt.kts.backend.dto.returnDTO.TypeDTO;
import nwt.kts.backend.entity.Type;
import nwt.kts.backend.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping(value = "/get-all-vehicle-types")
    @PreAuthorize("hasAnyRole('ADMIN', 'PASSENGER')")
    public ResponseEntity<List<TypeDTO>> getAllVehicles() {
        List<Type> types = vehicleService.getAllVehicleTypes();
        List<TypeDTO> typeDTOs = new ArrayList<>();
        for (Type type : types) {
            typeDTOs.add(new TypeDTO(type));
        }
        return new ResponseEntity<>(typeDTOs, HttpStatus.OK);
    }
}
