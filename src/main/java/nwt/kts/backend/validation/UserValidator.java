package nwt.kts.backend.validation;

import nwt.kts.backend.dto.creation.DriverCreationDTO;
import nwt.kts.backend.dto.creation.PassengerCreationDTO;
import nwt.kts.backend.dto.creation.UpdatedUserDataCreationDTO;
import nwt.kts.backend.exceptions.InvalidUserDataException;

import java.util.HashMap;
import java.util.Map;

public class UserValidator {

    private final String EMAIL_REGEX = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$";
    private final String PHONE_NUMBER_REGEX = "^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{2}[-\\s.]?[0-9]{5,7}$";
    private final String FIRST_LETTER_UPPER_CASE_REGEX = "[A-Z]\\w*";
    private final String CITY_REGEX = "^([a-zA-Z\\u0080-\\u024F]+(?:. |-| |'))*[a-zA-Z\\u0080-\\u024F]*$";

    private final String EMAIL_EXCEPTION = "email";
    private final String PHONE_NUMBER_EXCEPTION = "phone_number";
    private final String PASSWORD_EXCEPTION = "password";
    private final String NAME_EXCEPTION = "name";
    private final String SURNAME_EXCEPTION = "surname";
    private final String CITY_EXCEPTION = "city";

    private final String EMAIL_EXCEPTION_MESSAGE = "Email wasn't entered correctly.";
    private final String PHONE_NUMBER_EXCEPTION_MESSAGE = "Phone number wasn't entered correctly. It should match: ....";
    private final String PASSWORD_EXCEPTION_MESSAGE = "Password or password confirmation wasn't entered correctly, or they don't match.";
    private final String NAME_EXCEPTION_MESSAGE = "Name wasn't entered correctly. First letter should be upper case.";
    private final String SURNAME_EXCEPTION_MESSAGE = "Surname wasn't entered correctly. First letter should be upper case.";
    private final String CITY_EXCEPTION_MESSAGE = "City wasn't entered correctly. First letter(s) should be upper case.";

    private final Map<String, String> exceptions = new HashMap<String, String>() {{
            put(EMAIL_EXCEPTION, EMAIL_EXCEPTION_MESSAGE);
            put(PHONE_NUMBER_EXCEPTION, PHONE_NUMBER_EXCEPTION_MESSAGE);
            put(PASSWORD_EXCEPTION, PASSWORD_EXCEPTION_MESSAGE);
            put(NAME_EXCEPTION, NAME_EXCEPTION_MESSAGE);
            put(SURNAME_EXCEPTION, SURNAME_EXCEPTION_MESSAGE);
            put(CITY_EXCEPTION, CITY_EXCEPTION_MESSAGE);
        }};


    public void validateNewUser(DriverCreationDTO driverCreationDTO) {
        validateEmail(driverCreationDTO.getEmail());
        validatePhoneNumber(driverCreationDTO.getPhoneNumber());
        validatePasswords(driverCreationDTO.getPassword(), driverCreationDTO.getPasswordConfirmation());
        validateName(driverCreationDTO.getName());
        validateSurname(driverCreationDTO.getSurname());
        validateCity(driverCreationDTO.getCity());
    }

    public void validateNewPassenger(PassengerCreationDTO passengerCreationDTO) {
        validateEmail(passengerCreationDTO.getEmail());
        validatePhoneNumber(passengerCreationDTO.getPhoneNumber());
        validatePasswords(passengerCreationDTO.getPassword(), passengerCreationDTO.getPasswordConfirm());
        validateName(passengerCreationDTO.getName());
        validateSurname(passengerCreationDTO.getSurname());
        validateCity(passengerCreationDTO.getCity());
    }

    public void validateUpdatedUserData(UpdatedUserDataCreationDTO updatedUserDataCreationDTO) {
        validateName(updatedUserDataCreationDTO.getName());
        validateSurname(updatedUserDataCreationDTO.getSurname());
        validateCity(updatedUserDataCreationDTO.getCity());
        validatePhoneNumber(updatedUserDataCreationDTO.getPhoneNumber());
    }

    public void validateEmail(String email) {
        if (!email.matches(EMAIL_REGEX)){
            throw new InvalidUserDataException(exceptions.get(EMAIL_EXCEPTION));
        }
    }

    public void validatePhoneNumber(String phoneNumber) {
        if (!phoneNumber.matches(PHONE_NUMBER_REGEX)){
            throw new InvalidUserDataException(exceptions.get(PHONE_NUMBER_EXCEPTION));
        }
    }

    public void validatePasswords(String password, String passwordConfirmation) {
        if (password == null || !password.equals(passwordConfirmation)){
            throw new InvalidUserDataException(exceptions.get(PASSWORD_EXCEPTION));
        }
    }

    public void validateName(String name) {
        if (name.equals("") || !name.matches(FIRST_LETTER_UPPER_CASE_REGEX)){
            throw new InvalidUserDataException(exceptions.get(NAME_EXCEPTION));
        }
    }

    public void validateSurname(String surname) {
        if (surname.equals("") || !surname.matches(FIRST_LETTER_UPPER_CASE_REGEX)){
            throw new InvalidUserDataException(exceptions.get(SURNAME_EXCEPTION));
        }
    }

    public void validateCity(String city) {
        if (city.equals("") || !city.matches(CITY_REGEX)){
            throw new InvalidUserDataException(exceptions.get(CITY_EXCEPTION));
        }
    }
}
