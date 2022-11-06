package nwt.kts.backend.service;

import nwt.kts.backend.dto.creation.DriverCreationDTO;
import nwt.kts.backend.dto.creation.PasswordChangeCreationDTO;
import nwt.kts.backend.dto.creation.ProfilePictureCreationDTO;
import nwt.kts.backend.dto.creation.UpdatedUserDataCreationDTO;
import nwt.kts.backend.entity.*;
import nwt.kts.backend.repository.DriverDataRepository;
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

    @Autowired
    private UserService userService;

    /**
     * Repositories
     */

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private DriverDataRepository driverDataRepository;

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

    public DriverData sendUpdateRequest(UpdatedUserDataCreationDTO updatedUserDataCreationDTO) {
        userValidator.validateUpdatedUserData(updatedUserDataCreationDTO);
        return driverDataRepository.save(new DriverData(updatedUserDataCreationDTO));
    }

    public Driver changePassword(PasswordChangeCreationDTO passwordChangeCreationDTO) {
        User user = userService.changePassword(passwordChangeCreationDTO);
        return driverRepository.findDriverByEmail(user.getEmail());
    }

    public Driver changeProfilePicture(ProfilePictureCreationDTO profilePictureCreationDTO) {
        User user = userService.changeProfilePicture(profilePictureCreationDTO);
        return driverRepository.findDriverByEmail(user.getEmail());
    }
}
