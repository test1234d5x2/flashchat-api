package example.flashchat.repositories;

import example.flashchat.models.Follow;
import example.flashchat.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepo extends JpaRepository<Follow, String> {
    public Optional<Follow> findByFollowerAndFollowed(User follower, User followed);
}
