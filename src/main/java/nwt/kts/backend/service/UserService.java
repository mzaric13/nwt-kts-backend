package nwt.kts.backend.service;

import nwt.kts.backend.dto.creation.PasswordChangeCreationDTO;
import nwt.kts.backend.dto.creation.ProfilePictureCreationDTO;
import nwt.kts.backend.entity.User;
import nwt.kts.backend.repository.UserRepository;
import nwt.kts.backend.validation.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    /**
     * Repositories
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Validators
     */
    private final UserValidator userValidator = new UserValidator();


    public User updatePersonalUserInfo(User user, String name, String surname, String city, String phoneNumber){
        setPersonalInfo(user, name, surname, city, phoneNumber);
        return userRepository.save(user);
    }

    public User changePassword(PasswordChangeCreationDTO passwordChangeCreationDTO){
        userValidator.validatePasswords(passwordChangeCreationDTO.getNewPassword(), passwordChangeCreationDTO.getNewPasswordConfirmation());
        User user = userRepository.findUserByEmail(passwordChangeCreationDTO.getEmail());
        user.setPassword(passwordChangeCreationDTO.getNewPassword());
        return userRepository.save(user);
    }

    public User changeProfilePicture(ProfilePictureCreationDTO profilePictureCreationDTO){
        User user = userRepository.findUserByEmail(profilePictureCreationDTO.getEmail());
        user.setProfilePicture(profilePictureCreationDTO.getProfilePicturePath());
        return userRepository.save(user);
    }

    private void setPersonalInfo(User user, String name, String surname, String city, String phoneNumber) {
        user.setName(name);
        user.setSurname(surname);
        user.setCity(city);
        user.setPhoneNumber(phoneNumber);
    }

}
