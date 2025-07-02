package example.flashchat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import example.flashchat.models.Comment;

@Repository
public interface CommentRepo extends JpaRepository<Comment, String> {
    List<Comment> findByPostId(String postId);
}