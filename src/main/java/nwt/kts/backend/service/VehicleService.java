package nwt.kts.backend.service;

import nwt.kts.backend.dto.creation.VehicleCreationDTO;
import nwt.kts.backend.entity.Type;
import nwt.kts.backend.repository.TypeRepository;
import nwt.kts.backend.repository.VehicleRepository;
import nwt.kts.backend.validation.VehicleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private TypeRepository typeRepository;

    private final VehicleValidator vehicleValidator = new VehicleValidator();

    public void validateNewVehicle(VehicleCreationDTO vehicleCreationDTO) {
        vehicleValidator.validateNewVehicle(vehicleCreationDTO);
    }

    public List<Type> getAllVehicleTypes() {
        return typeRepository.findAll();
    }
}
