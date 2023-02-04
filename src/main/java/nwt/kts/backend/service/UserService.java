package nwt.kts.backend.service;

import nwt.kts.backend.dto.creation.PasswordChangeCreationDTO;
import nwt.kts.backend.dto.creation.ProfilePictureCreationDTO;
import nwt.kts.backend.dto.returnDTO.PasswordResetDTO;
import nwt.kts.backend.entity.ImageData;
import nwt.kts.backend.entity.User;
import nwt.kts.backend.repository.ImageDataRepository;
import nwt.kts.backend.repository.UserRepository;
import nwt.kts.backend.util.ImageUtil;
import nwt.kts.backend.validation.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional
public class UserService {

    /**
     * Repositories
     */
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ImageDataRepository imageDataRepository;

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
        user.setPassword(passwordEncoder.encode(passwordChangeCreationDTO.getNewPassword()));
        return userRepository.save(user);
    }

    public User changeProfilePicture(String email, MultipartFile file) throws IOException {
        User user = userRepository.findUserByEmail(email);
        ImageData imageData = imageDataRepository.save(ImageData.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imageData(ImageUtil.compressImage(file.getBytes())).build());
        user.setProfilePictureData(imageData);
        return userRepository.save(user);
    }

    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    private void setPersonalInfo(User user, String name, String surname, String city, String phoneNumber) {
        user.setName(name);
        user.setSurname(surname);
        user.setCity(city);
        user.setPhoneNumber(phoneNumber);
    }

    public User resetPassword(PasswordResetDTO passwordResetDTO, String email) {
        userValidator.validatePasswords(passwordResetDTO.getPassword(), passwordResetDTO.getConfirmPassword());
        User user = userRepository.findUserByEmail(email);
        user.setPassword(passwordEncoder.encode(passwordResetDTO.getPassword()));
        return userRepository.save(user);
    }

}
