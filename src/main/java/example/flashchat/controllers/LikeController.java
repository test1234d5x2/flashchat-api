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
        public boolean addLike(@RequestBody LikeRequest request) {
        String postId = request.postId;
        String userId = request.userId;

        if (postId.isEmpty() || userId.isEmpty()) {
            // Empty check.
            return false;
        }
        
        if (!postService.postExists(postId) || !userService.userExists(userId)) {
            // Post and user existence check.
            return false;
        }
        
        Post p = postService.retrievePostById(postId);
        User u = userService.findById(userId);

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
    public boolean deleteLike(@RequestBody LikeRequest request) {
        String postId = request.postId;
        String userId = request.userId;

        if (postId.isEmpty() || userId.isEmpty()) {
            // Empty check.
            return false;
        }

        if (!postService.postExists(postId) || !userService.userExists(userId)) {
            // Post and user existence check.
            return false;
        }

        Post p = postService.retrievePostById(postId);
        User u = userService.findById(userId);

        if (!likeService.likeExists(p, u)) {
            // Check if like exists.
            return false;
        }

        return likeService.deleteLike(p, u);
    }


    @GetMapping
    public List<Like> getLikedPosts(@RequestParam String userId) {
        if (userId.isEmpty()) {
            // Empty check.
            return new ArrayList<>();
        }

        if (!userService.userExists(userId)) {
            // User existence check.
            return new ArrayList<>();
        }

        return userService.findById(userId).getLikedPosts();
    }


    @PostMapping("/check-like")
    public boolean checkLike(@RequestBody LikeRequest request) {
        String postId = request.postId;
        String userId = request.userId;

        if (postId.isEmpty() || userId.isEmpty()) {
            // Empty check.
            return false;
        }

        if (!postService.postExists(postId) || !userService.userExists(userId)) {
            // Post and user existence check.
            return false;
        }

        Post p = postService.retrievePostById(postId);
        User u = userService.findById(userId);

        if (likeService.likeExists(p, u)) {
            // Check if like exists.
            return true;
        }

        return false;
    }
}


class LikeRequest {
    public String postId;
    public String userId;
}