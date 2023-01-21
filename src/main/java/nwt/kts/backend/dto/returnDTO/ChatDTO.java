package nwt.kts.backend.dto.returnDTO;

import nwt.kts.backend.entity.Chat;

public class ChatDTO {

    private int id;

    private String chatName;

    public ChatDTO() {
    }

    public ChatDTO(int id, String chatName) {
        this.id = id;
        this.chatName = chatName;
    }

    public ChatDTO(Chat chat) {
        this.id = chat.getId();
        this.chatName = chat.getChatName();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }
}
