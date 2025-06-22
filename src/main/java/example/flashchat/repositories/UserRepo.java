package example.flashchat.repositories;

import example.flashchat.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {
    public List<User> findByUsernameAndPassword(String username, String password);
    public Optional<User> findByUsername(String username);
}
