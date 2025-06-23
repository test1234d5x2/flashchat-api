package example.flashchat.controllers;

import example.flashchat.models.User;
import example.flashchat.requestStructures.FollowRequest;
import example.flashchat.services.FollowService;
import example.flashchat.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;
import example.flashchat.models.Follow;
import example.flashchat.requestStructures.IdRequest;

@RestController
@RequestMapping("api/v1/follows")
public class FollowController {

    @Autowired
    private FollowService followService;

    @Autowired
    private UserService userService;

    @PostMapping
    public boolean follow(@RequestBody FollowRequest followRequest) {
        final String followerId = followRequest.followerId;
        final String followedId = followRequest.followedId;

        if (followedId.isEmpty() || followerId.isEmpty()) {
            // Empty check.
            return false;
        }

        if (!userService.userExists(followerId) || !userService.userExists(followedId)) {
            // Check users exist.
            return false;
        }

        User follower = userService.findById(followerId);
        User followed = userService.findById(followedId);

        if (followService.followExists(follower, followed)) {
            // Check if the follower already follows the user.
            return false;
        }

        return followService.addFollow(follower, followed);
    }


    @DeleteMapping
    public boolean removeFollow(@RequestBody FollowRequest followRequest) {
        final String followerId = followRequest.followerId;
        final String followedId = followRequest.followedId;

        if (followedId.isEmpty() || followerId.isEmpty()) {
            // Empty check.
            return false;
        }

        if (!userService.userExists(followerId) || !userService.userExists(followedId)) {
            // Check users exist.
            return false;
        }

        User follower = userService.findById(followerId);
        User followed = userService.findById(followedId);

        if (!followService.followExists(follower, followed)) {
            // Check if the follower does not follow the user.
            return false;
        }

        return followService.removeFollow(follower, followed);
    }


    @GetMapping("/followers")
    public List<Follow> getFollowers(@RequestBody IdRequest idRequest) {
        final String userId = idRequest.id;

        if (userId.isEmpty()) {
            // Empty check.
            return new ArrayList<>();
        }

        if (!userService.userExists(userId)) {
            // User existence check.
            return new ArrayList<>();
        }

        return userService.findById(userId).getFollowers();
    }

    @GetMapping("/following")
    public List<Follow> getFollowing(@RequestBody IdRequest idRequest) {
        final String userId = idRequest.id;

        if (userId.isEmpty()) {
            // Empty check.
            return new ArrayList<>();
        }

        if (!userService.userExists(userId)) {
            // User existence check.
            return new ArrayList<>();
        }

        return userService.findById(userId).getFollowedBy();
    }
}
