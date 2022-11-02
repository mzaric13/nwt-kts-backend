package nwt.kts.backend.service;

import nwt.kts.backend.dto.creation.DriverCreationDTO;
import nwt.kts.backend.entity.Driver;
import nwt.kts.backend.entity.Role;
import nwt.kts.backend.entity.Type;
import nwt.kts.backend.repository.DriverRepository;
import nwt.kts.backend.repository.UserRepository;
import nwt.kts.backend.validation.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DriverService {

    /**
     * Services
     */
    @Autowired
    private RoleService roleService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private VehicleService vehicleService;


    /**
     * Repositories
     */
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DriverRepository driverRepository;


    /**
     * Constants
     */
    private final String DRIVER_NAME = "driver";


    /**
     * Validators
     */
    private final UserValidator userValidator = new UserValidator();


    public Driver createDriver(DriverCreationDTO driverCreationDTO) {
        userValidator.validateNewUser(driverCreationDTO);
        vehicleService.validateNewVehicle(driverCreationDTO.getVehicleCreationDTO());
        Role role = roleService.findRoleByName(DRIVER_NAME);
        Type type = typeService.findTypeByName(driverCreationDTO.getVehicleCreationDTO().getType());
        return driverRepository.save(new Driver(driverCreationDTO, role, type));
    }
}
