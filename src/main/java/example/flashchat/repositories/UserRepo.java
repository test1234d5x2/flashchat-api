package example.flashchat.repositories;

import example.flashchat.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, String> {
    public Optional<User> findByUsername(String username);
}
