package nwt.kts.backend.entity;

import nwt.kts.backend.dto.returnDTO.ChatDTO;

import javax.persistence.*;

@Entity
@Table(name="chats")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name="chat_name", unique = true)
    private String chatName;

    public Chat() {
    }

    public Chat(String chatName) {
        this.chatName = chatName;
    }

    public Chat(ChatDTO chatDTO) {
        this.id = chatDTO.getId();
        this.chatName = chatDTO.getChatName();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }
}
