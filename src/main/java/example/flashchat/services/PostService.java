package example.flashchat.services;

import example.flashchat.models.Post;
import example.flashchat.repositories.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepo postRepo;

    public boolean postExists(String id) {
        return postRepo.findById(id).isPresent();
    }

    public Post retrievePostById(String id) {
        return postRepo.findById(id).orElse(null);
    }

    public boolean createPost(Post post) {
        postRepo.save(post);
        return true;
    }

    public List<Post> getPosts(String user_id) {
        return postRepo.findByUserId(user_id);
    }

    public boolean deletePost(String id) {
        postRepo.deleteById(id);
        return true;
    }
}
