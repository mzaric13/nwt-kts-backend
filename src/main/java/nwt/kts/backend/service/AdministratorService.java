package nwt.kts.backend.service;

import nwt.kts.backend.dto.creation.AnsweredDriverDataCreationDTO;
import nwt.kts.backend.entity.DriverData;
import nwt.kts.backend.entity.User;
import nwt.kts.backend.repository.DriverDataRepository;
import nwt.kts.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdministratorService {

    /**
     * Services
     */
    @Autowired
    private DriverService driverService;

    @Autowired
    private UserService userService;

    /**
     * Repositories
     */
    @Autowired
    private DriverDataRepository driverDataRepository;

    @Autowired
    private UserRepository userRepository;


    public List<DriverData> getUnansweredDriverData() {
        return driverDataRepository.getAllByIsAnswered(false);
    }

    public DriverData answerDriverDataChange(AnsweredDriverDataCreationDTO answeredDriverDataCreationDTO) {

        DriverData answeredDriverDataChange = driverDataRepository.getDriverDataById(answeredDriverDataCreationDTO.getDriverDataId());
        User user = userRepository.findUserByEmail(answeredDriverDataChange.getEmail());

        if (answeredDriverDataCreationDTO.isApproved()) {
            userService.updatePersonalUserInfo(user, answeredDriverDataChange.getName(), answeredDriverDataChange.getSurname(),
                    answeredDriverDataChange.getCity(), answeredDriverDataChange.getPhoneNumber());
        }

        answeredDriverDataChange.setAnswered(true);
        return driverDataRepository.save(answeredDriverDataChange);
    }
}
