package example.flashchat.services;

import example.flashchat.models.Like;
import example.flashchat.models.Post;
import example.flashchat.models.User;
import example.flashchat.repositories.LikeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    private LikeRepo likeRepo;


    public boolean addLike(Post post, User user) {
        Like l = new Like();
        l.setLikedBy(user);
        l.setPostLiked(post);

        likeRepo.save(l);
        return true;
    }

    public boolean deleteLike(Post p, User u) {
        Optional<Like> like = likeRepo.findByPostLikedAndLikedBy(p, u);
        if (like.isEmpty()) {
            return false;
        }

        likeRepo.delete(like.get());
        return true;
    }

    public boolean likeExists(Post p, User u) {
        return likeRepo.findByPostLikedAndLikedBy(p, u).isPresent();
    }
}
