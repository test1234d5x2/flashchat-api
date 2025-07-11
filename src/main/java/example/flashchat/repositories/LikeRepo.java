package example.flashchat.repositories;

import example.flashchat.models.Like;
import example.flashchat.models.Post;
import example.flashchat.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepo extends JpaRepository<Like, String> {
    public Optional<Like> findByPostLikedAndLikedBy(Post post, User user);
}
