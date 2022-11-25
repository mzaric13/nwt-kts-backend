package nwt.kts.backend.dto.login;

public class GoogleTokenDTO {

    private String value;

    public GoogleTokenDTO() {
    }

    public GoogleTokenDTO(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
