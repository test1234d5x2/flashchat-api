package example.flashchat.controllers;

import example.flashchat.models.User;
import example.flashchat.services.FollowService;
import example.flashchat.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
    public boolean follow(Authentication authentication, @RequestBody FollowRequest request) {
        if (authentication == null) {
            System.out.println("No authentication present");
            return false;
        }

        String username = authentication.getName().toString(); // follower
        String followedId = request.followedId;

        if (followedId.isEmpty()) {
            // Empty check.
            return false;
        }

        if (!userService.userExistsByUsername(username) || !userService.userExists(followedId)) {
            // Check users exist.
            return false;
        }

        User follower = userService.findByUsername(username);
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
    public boolean removeFollow(Authentication authentication, @RequestBody FollowRequest request) {
        if (authentication == null) {
            System.out.println("No authentication present");
            return false;
        }

        String username = authentication.getName().toString(); // follower
        String followedId = request.followedId;

        if (followedId.isEmpty()) {
            // Empty check.
            return false;
        }

        if (!userService.userExistsByUsername(username) || !userService.userExists(followedId)) {
            // Check users exist.
            return false;
        }

        User follower = userService.findByUsername(username);
        User followed = userService.findById(followedId);

        if (!followService.followExists(follower, followed)) {
            // Check if the follower does not follow the user.
            return false;
        }

        return followService.removeFollow(follower, followed);
    }


    @GetMapping("/followers/{userId}")
    public List<Follow> getFollowers(Authentication authentication) {
        if (authentication == null) {
            System.out.println("No authentication present");
            return new ArrayList<>();
        }

        String username = authentication.getName().toString();

        if (!userService.userExistsByUsername(username)) {
            // User existence check.
            return new ArrayList<>();
        }

        return userService.findByUsername(username).getFollowers();
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
    public boolean checkFollow(Authentication authentication, @RequestBody FollowRequest request) {
        if (authentication == null) {
            System.out.println("No authentication present");
            return false;
        }

        String username = authentication.getName().toString(); // follower
        String followedId = request.followedId;

        if (followedId.isEmpty()) {
            // Empty check.
            return false;
        }

        if (!userService.userExistsByUsername(username) || !userService.userExists(followedId)) {
            // Check users exist.
            return false;
        }

        User follower = userService.findByUsername(username);
        User followed = userService.findById(followedId);

        if (followService.followExists(follower, followed)) {
            // Check if the follower follows the user.
            return true;
        }

        return false;
    } // TODO: Needs Testing
}


class FollowRequest {
    public String followedId;
}