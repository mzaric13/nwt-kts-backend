package nwt.kts.backend.controller;

import nwt.kts.backend.dto.creation.MessageCreationDTO;
import nwt.kts.backend.dto.returnDTO.MessageDTO;
import nwt.kts.backend.entity.Chat;
import nwt.kts.backend.entity.Message;
import nwt.kts.backend.service.ChatService;
import nwt.kts.backend.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'PASSENGER', 'DRIVER')")
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ChatService chatService;

    @Autowired
    private MessageService messageService;

    @MessageMapping("/chat/{to}")
    public void sendMessage(@DestinationVariable String to, MessageCreationDTO messageCreationDTO) {
        Message message = new Message(messageCreationDTO);
        message.setChat(chatService.getChat(to));
        message.setTimestamp(generateTimestamp());
        message = messageService.saveMessage(message);
        simpMessagingTemplate.convertAndSend("/topic/messages/" + to, new MessageDTO(message));
    }

    @GetMapping("/get-user-messages/{chatName}")
    public ResponseEntity<List<MessageDTO>> getUserMessages(@PathVariable String chatName) {
        Chat chat = chatService.findByName(chatName);
        if (chat != null) {
            List<Message> messages = messageService.getAllChatMessages(chat);
            List<MessageDTO> messageDTOs = messages.stream().map(MessageDTO::new).collect(Collectors.toList());
            return new ResponseEntity<>(messageDTOs, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
    }

    private Timestamp generateTimestamp() {
        return new Timestamp(new Date().getTime());
    }
}
