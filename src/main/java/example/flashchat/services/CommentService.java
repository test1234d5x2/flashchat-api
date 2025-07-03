package example.flashchat.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import example.flashchat.models.Comment;
import example.flashchat.repositories.CommentRepo;

@Service
public class CommentService {

    @Autowired
    private CommentRepo commentRepo;

    public boolean createComment(Comment comment) {
        commentRepo.save(comment);
        return true;
    }

    public boolean deleteComment(String id) {
        commentRepo.deleteById(id);
        return true;
    }

    public boolean commentExists(String id) {
        return commentRepo.existsById(id);
    }

    public Comment getComment(String id) {
        return commentRepo.findById(id).orElse(null);
    }

    public List<Comment> getComments(String postId) {
        return commentRepo.findByPostId(postId);
    }
}