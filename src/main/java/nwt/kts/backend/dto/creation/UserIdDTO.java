package nwt.kts.backend.dto.creation;

public class UserIdDTO {

    Integer id;
    String reasoning;

    public UserIdDTO() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReasoning() {
        return reasoning;
    }

    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }
}
