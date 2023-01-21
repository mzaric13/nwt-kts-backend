package nwt.kts.backend.repository;

import nwt.kts.backend.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Integer> {

    Chat findByChatName(String chatName);
}
