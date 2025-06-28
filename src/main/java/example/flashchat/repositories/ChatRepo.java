package example.flashchat.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import example.flashchat.models.Chat;

@Repository
public interface ChatRepo extends JpaRepository<Chat, String> {
    public List<Chat> findByUser1IdOrUser2Id(String userId1, String userId2);
    public Optional<Chat> findByUser1IdAndUser2Id(String userId1, String userId2);
}
