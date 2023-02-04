package nwt.kts.backend.dto.returnDTO;

import nwt.kts.backend.entity.Message;

import java.sql.Timestamp;
import java.util.Objects;

public class MessageDTO {

    private int id;
    private ChatDTO chatDTO;
    private String sender;
    private Timestamp timestamp;
    private String message;

    public MessageDTO() {
    }

    public MessageDTO(int id, ChatDTO chatDTO, String sender, Timestamp timestamp, String message) {
        this.id = id;
        this.chatDTO = chatDTO;
        this.sender = sender;
        this.timestamp = timestamp;
        this.message = message;
    }

    public MessageDTO(Message message) {
        this.id = message.getId();
        this.chatDTO = new ChatDTO(message.getChat());
        this.sender = message.getSender();
        this.timestamp = message.getTimestamp();
        this.message = message.getMessage();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ChatDTO getChatDTO() {
        return chatDTO;
    }

    public void setChatDTO(ChatDTO chatDTO) {
        this.chatDTO = chatDTO;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageDTO that = (MessageDTO) o;
        return id == that.id;
    }

}
