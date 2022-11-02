package nwt.kts.backend.validation;

import nwt.kts.backend.dto.creation.VehicleCreationDTO;
import nwt.kts.backend.exceptions.InvalidVehicleDataException;

import java.util.HashMap;
import java.util.Map;

public class VehicleValidator {

    private final String REGISTRATION_REGEX = "([A-Za-z])([A-Za-z])([0-9][0-9][0-9])([A-Za-z])([A-Za-z])";

    private final String NAME_EXCEPTION = "name";
    private final String REGISTRATION_NUMBER_EXCEPTION = "registration_number";
    private final String TYPE_EXCEPTION = "type";

    private final String NAME_EXCEPTION_MESSAGE = "Vehicle name wasn't entered correctly.";
    private final String REGISTRATION_NUMBER_EXCEPTION_MESSAGE = "Registration number wasn't entered correctly. " +
            "Format: XXYYYXX, where X is a character [A-Z] and Y is a number [0-9].";
    private final String TYPE_EXCEPTION_MESSAGE = "Type wasn't entered correctly.";

    private final Map<String, String> exceptions = new HashMap<String, String>() {{
        put(NAME_EXCEPTION, NAME_EXCEPTION_MESSAGE);
        put(REGISTRATION_NUMBER_EXCEPTION, REGISTRATION_NUMBER_EXCEPTION_MESSAGE);
        put(TYPE_EXCEPTION, TYPE_EXCEPTION_MESSAGE);
    }};

    public void validateNewVehicle(VehicleCreationDTO vehicleCreationDTO) {
        validateName(vehicleCreationDTO.getName());
        validateRegistrationNumber(vehicleCreationDTO.getRegistrationNumber());
        validateType(vehicleCreationDTO.getType());
    }

    public void validateName(String name) {
        if (name == null) {
            throw new InvalidVehicleDataException(exceptions.get(NAME_EXCEPTION));
        }
    }

    public void validateRegistrationNumber(String registrationNumber) {
        if (!registrationNumber.matches(REGISTRATION_REGEX)){
            throw new InvalidVehicleDataException(exceptions.get(REGISTRATION_NUMBER_EXCEPTION));
        }
    }

    public void validateType(String typeName) {
        if (typeName == null){
            throw new InvalidVehicleDataException(exceptions.get(TYPE_EXCEPTION));
        }
    }
}
