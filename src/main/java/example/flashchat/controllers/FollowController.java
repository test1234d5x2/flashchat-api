package example.flashchat.controllers;

import example.flashchat.models.User;
import example.flashchat.services.FollowService;
import example.flashchat.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;
import example.flashchat.models.Follow;
import example.flashchat.services.NotificationService;
import example.flashchat.models.Notification;
import example.flashchat.enums.NotificationType;

@RestController
@RequestMapping("api/v1/follows")
public class FollowController {

    @Autowired
    private FollowService followService;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public boolean follow(@RequestBody FollowRequest request) {
        String followerId = request.followerId;
        String followedId = request.followedId;

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

        Follow f = new Follow();
        f.setFollower(follower);
        f.setFollowed(followed);

        boolean success = followService.addFollow(f);

        if (success) {
            Notification notification = new Notification();
            notification.setMessage("Followed you");
            notification.setType(NotificationType.FOLLOW);
            notification.setRecepientUser(followed);
            notification.setActionUser(follower);
            notificationService.createNotification(notification);
        }

        return success;
    }


    @DeleteMapping
    public boolean removeFollow(@RequestBody FollowRequest request) {
        String followerId = request.followerId;
        String followedId = request.followedId;

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


    @GetMapping("/followers/{userId}")
    public List<Follow> getFollowers(@PathVariable String userId) {
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

    @GetMapping("/following/{userId}")
    public List<Follow> getFollowing(@PathVariable String userId) {
        if (userId.isEmpty()) {
            // Empty check.
            return new ArrayList<>();
        }

        if (!userService.userExists(userId)) {
            // User existence check.
            return new ArrayList<>();
        }

        return userService.findById(userId).getFollowing();
    }


    @PostMapping("/check-follow")
    public boolean checkFollow(@RequestBody FollowRequest request) {
        String followerId = request.followerId;
        String followedId = request.followedId;

        if (followerId.isEmpty() || followedId.isEmpty()) {
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
            // Check if the follower follows the user.
            return true;
        }

        return false;
    }
}


class FollowRequest {
    public String followerId;
    public String followedId;
}