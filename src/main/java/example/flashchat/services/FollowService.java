package example.flashchat.services;

import example.flashchat.models.Follow;
import example.flashchat.models.User;
import example.flashchat.repositories.FollowRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FollowService {

    @Autowired
    private FollowRepo followRepo;

    public boolean addFollow(Follow f) {
        followRepo.save(f);
        return true;
    }

    public boolean removeFollow(User follower, User followed) {
        Optional<Follow> follow = followRepo.findByFollowerAndFollowed(follower, followed);
        if (follow.isEmpty()) {
            return false;
        }

        followRepo.delete(follow.get());
        return true;
    }

    public boolean followExists(User follower, User followed) {
        return followRepo.findByFollowerAndFollowed(follower, followed).isPresent();
    }
}
