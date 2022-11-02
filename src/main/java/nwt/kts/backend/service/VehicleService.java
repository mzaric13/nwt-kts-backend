package nwt.kts.backend.service;

import nwt.kts.backend.dto.creation.VehicleCreationDTO;
import nwt.kts.backend.repository.VehicleRepository;
import nwt.kts.backend.validation.VehicleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    private final VehicleValidator vehicleValidator = new VehicleValidator();

    public void validateNewVehicle(VehicleCreationDTO vehicleCreationDTO) {
        vehicleValidator.validateNewVehicle(vehicleCreationDTO);
    }
}
