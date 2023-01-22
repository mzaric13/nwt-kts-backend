package nwt.kts.backend.service;

import nwt.kts.backend.entity.Chat;
import nwt.kts.backend.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    public Chat saveChat(Chat chat) {
        return chatRepository.save(chat);
    }

    public Chat findByName(String chatName) {
        return chatRepository.findByChatName(chatName);
    }

    public Chat getChat(String chatName) {
        Chat chat = findByName(chatName);
        if (chat != null) {
            return chat;
        } else {
            Chat newChat = new Chat(chatName);
            return saveChat(newChat);
        }
    }
}
