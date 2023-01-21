package nwt.kts.backend.entity;

import nwt.kts.backend.dto.creation.MessageCreationDTO;
import nwt.kts.backend.dto.returnDTO.MessageDTO;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name="messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Column(name="sender")
    private String sender;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    @Column(name = "message")
    private String message;

    public Message() {
    }

    public Message(Chat chat, String sender, Timestamp timestamp, String message) {
        this.chat = chat;
        this.sender = sender;
        this.timestamp = timestamp;
        this.message = message;
    }

    public Message(MessageDTO messageDTO) {
        this.id = messageDTO.getId();
        this.chat = new Chat(messageDTO.getChatDTO());
        this.sender = messageDTO.getSender();
        this.timestamp = messageDTO.getTimestamp();
        this.message = messageDTO.getMessage();
    }
    public Message(MessageCreationDTO messageCreationDTO) {
        this.sender = messageCreationDTO.getSender();
        this.message = messageCreationDTO.getMessage();
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
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
}
