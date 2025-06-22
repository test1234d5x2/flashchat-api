package example.flashchat.repositories;

import example.flashchat.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepo extends JpaRepository<Post, String> {
    public List<Post> findByUserId(String user_id);
}
