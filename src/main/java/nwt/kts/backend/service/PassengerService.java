package nwt.kts.backend.service;

import nwt.kts.backend.dto.creation.PassengerCreationDTO;
import nwt.kts.backend.entity.Passenger;
import nwt.kts.backend.entity.Role;
import nwt.kts.backend.exceptions.NonExistingEntityException;
import nwt.kts.backend.repository.PassengerRepository;
import nwt.kts.backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.Optional;

@Service
public class PassengerService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private EmailService emailService;

    public Passenger createPassenger(PassengerCreationDTO passengerCreationDTO) throws MessagingException {
        // TODO: add validation
        Role role = roleRepository.findRoleByName("passenger");
        //if (role == null) throw new NonValidDataException("Passenger role doesn't exist.");
        Passenger passenger = new Passenger(passengerCreationDTO.getEmail(), passengerCreationDTO.getPhoneNumber(), passengerCreationDTO.getPassword(), passengerCreationDTO.getName(), passengerCreationDTO.getSurname(), passengerCreationDTO.getCity(), role, false, false);
        passenger = passengerRepository.save(passenger);
        emailService.sendActivationEmail(passenger);
        return passengerRepository.save(passenger);
    }

    public Passenger activateAccount(Integer id) {
        Optional<Passenger> passenger = passengerRepository.findById(id);
        if (passenger.isPresent()) {
            Passenger pass = passenger.get();
            pass.setActivated(true);
            return passengerRepository.save(pass);
        }else throw new NonExistingEntityException("Passenger with given id doesn't exist.");
    }
}
