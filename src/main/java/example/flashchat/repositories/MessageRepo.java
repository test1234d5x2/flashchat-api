package example.flashchat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import example.flashchat.models.Message;

@Repository
public interface MessageRepo extends JpaRepository<Message, String> {
    List<Message> findByChatId(String chatId);
}
