package nwt.kts.backend.service;

import nwt.kts.backend.entity.Chat;
import nwt.kts.backend.entity.Message;
import nwt.kts.backend.repository.ChatRepository;
import nwt.kts.backend.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private MessageRepository messageRepository;

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

    public Message createMessage(String content, Chat chat, String sender) {
        Message message = new Message(chat, sender, new Timestamp(new Date().getTime()), content);
        return messageRepository.save(message);
    }
}
