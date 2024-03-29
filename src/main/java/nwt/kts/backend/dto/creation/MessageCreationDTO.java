package nwt.kts.backend.dto.creation;

public class MessageCreationDTO {

    private String sender;
    private String message;

    public MessageCreationDTO() {
    }

    public MessageCreationDTO(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
