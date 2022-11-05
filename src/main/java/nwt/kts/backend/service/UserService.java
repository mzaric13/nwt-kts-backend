package nwt.kts.backend.service;

import nwt.kts.backend.entity.User;
import nwt.kts.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User updatePersonalUserInfo(User user, String name, String surname, String city, String phoneNumber){
        setPersonalInfo(user, name, surname, city, phoneNumber);
        return userRepository.save(user);
    }

    private void setPersonalInfo(User user, String name, String surname, String city, String phoneNumber) {
        user.setName(name);
        user.setSurname(surname);
        user.setCity(city);
        user.setPhoneNumber(phoneNumber);
    }
}
