package nwt.kts.backend.repository;

import nwt.kts.backend.entity.Chat;
import nwt.kts.backend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    List<Message> findAllByChatOrderById(Chat chat);
}
