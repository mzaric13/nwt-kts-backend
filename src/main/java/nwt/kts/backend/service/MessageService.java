package nwt.kts.backend.service;

import nwt.kts.backend.entity.Chat;
import nwt.kts.backend.entity.Message;
import nwt.kts.backend.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> getAllChatMessages(Chat chat) {
        return messageRepository.findAllByChatOrderById(chat);
    }
}
