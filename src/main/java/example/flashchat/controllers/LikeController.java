package example.flashchat.controllers;

import example.flashchat.models.Like;
import example.flashchat.models.Post;
import example.flashchat.models.User;
import example.flashchat.models.Notification;
import example.flashchat.enums.NotificationType;
import example.flashchat.services.NotificationService;
import example.flashchat.services.LikeService;
import example.flashchat.services.PostService;
import example.flashchat.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public boolean addLike(Authentication authentication, @RequestBody LikeRequest request) {
        if (authentication == null) {
            System.out.println("No authentication present");
            return false;
        }

        String username = authentication.getName().toString();
        String postId = request.postId;

        if (postId.isEmpty()) {
            // Empty check.
            return false;
        }
        
        if (!postService.postExists(postId) || !userService.userExistsByUsername(username)) {
            // Post and user existence check.
            return false;
        }
        
        Post p = postService.retrievePostById(postId);
        User u = userService.findByUsername(username);

        if (p.getUser().getId().equals(u.getId())) {
            // Check whether the user liking the post is the user that created the post.
            return false;
        }

        if (likeService.likeExists(p, u)) {
            // Check whether a like already exists for this user.
            return false;
        }

        Like l = new Like();
        l.setLikedBy(u);
        l.setPostLiked(p);

        boolean success = likeService.addLike(l);

        if (success) {
            Notification notification = new Notification();
            notification.setMessage("Liked your post");
            notification.setType(NotificationType.LIKE);
            notification.setRecepientUser(p.getUser());
            notification.setActionUser(u);
            notificationService.createNotification(notification);
        }

        return success;
    }


    @DeleteMapping
    public boolean deleteLike(Authentication authentication, @RequestBody LikeRequest request) {
        if (authentication == null) {
            System.out.println("No authentication present");
            return false;
        }

        String username = authentication.getName().toString();
        String postId = request.postId;

        if (postId.isEmpty()) {
            // Empty check.
            return false;
        }

        if (!postService.postExists(postId) || !userService.userExistsByUsername(username)) {
            // Post and user existence check.
            return false;
        }

        Post p = postService.retrievePostById(postId);
        User u = userService.findByUsername(username);

        if (!likeService.likeExists(p, u)) {
            // Check if like exists.
            return false;
        }

        return likeService.deleteLike(p, u);
    }


    @GetMapping
    public List<Like> getLikedPosts(Authentication authentication) {
        if (authentication == null) {
            System.out.println("No authentication present");
            return new ArrayList<>();
        }

        String username = authentication.getName().toString();

        if (!userService.userExistsByUsername(username)) {
            // User existence check.
            return new ArrayList<>();
        }

        return userService.findByUsername(username).getLikedPosts();
    }


    @PostMapping("/check-like")
    public boolean checkLike(Authentication authentication, @RequestBody LikeRequest request) {
        if (authentication == null) {
            System.out.println("No authentication present");
            return false;
        }

        String username = authentication.getName().toString();
        String postId = request.postId;

        if (postId.isEmpty()) {
            // Empty check.
            return false;
        }

        if (!postService.postExists(postId) || !userService.userExistsByUsername(username)) {
            // Post and user existence check.
            return false;
        }

        Post p = postService.retrievePostById(postId);
        User u = userService.findByUsername(username);

        if (likeService.likeExists(p, u)) {
            // Check if like exists.
            return true;
        }

        return false;
    }
}


class LikeRequest {
    public String postId;
}