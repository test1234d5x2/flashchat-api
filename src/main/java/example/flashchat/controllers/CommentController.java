package example.flashchat.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import example.flashchat.services.CommentService;
import example.flashchat.services.PostService;
import example.flashchat.services.UserService;
import example.flashchat.models.Comment;
import example.flashchat.models.Post;
import example.flashchat.models.User;
import example.flashchat.models.Notification;
import example.flashchat.enums.NotificationType;
import example.flashchat.services.NotificationService;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public boolean createComment(@RequestBody CommentRequest commentRequest) {
        String postId = commentRequest.postId;
        String commentText = commentRequest.comment;
        String userId = commentRequest.userId;
        Optional<String> parentCommentId = commentRequest.parentCommentId;

        if (postId == null || commentText == null || userId == null) {
            // Empty check for the params that need to be present.
            return false;
        }

        if (parentCommentId == null) {
            parentCommentId = Optional.empty();
        }
        
        if (parentCommentId.isPresent() && !commentService.commentExists(parentCommentId.get())) {
            // If available, check if the parent comment exists.
            return false;
        }

        if (!postService.postExists(postId) || !userService.userExists(userId)) {
            // If the post or user does not exist, return false.
            return false;
        }

        Post post = postService.retrievePostById(postId);
        User user = userService.findById(userId);
        
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setComment(commentText);
        comment.setParent(parentCommentId.isPresent() ? commentService.getComment(parentCommentId.get()) : null);
        
        boolean success = commentService.createComment(comment);

        if (success) {
            Notification notification = new Notification();
            notification.setMessage("Commented on your post");
            notification.setType(NotificationType.COMMENT);
            notification.setRecepientUser(post.getUser());
            notification.setActionUser(user);
            notificationService.createNotification(notification);
        }

        return success;
    }
}


class CommentRequest {
    public String userId;
    public String postId;
    public String comment;
    public Optional<String> parentCommentId;
}