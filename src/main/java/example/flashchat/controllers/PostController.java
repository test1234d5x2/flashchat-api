package example.flashchat.controllers;

import example.flashchat.models.Post;
import example.flashchat.models.User;
import example.flashchat.services.PostService;
import example.flashchat.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @PostMapping
    public boolean createPost(@RequestParam String userId, @RequestParam String post) {
        if (userId.isEmpty()) {
            return false;
        }

        if (!userService.userExists(userId) || post.isEmpty()) {
            return false;
        }

        User u = userService.findById(userId);
        Post p = new Post();
        p.setPost(post);
        p.setUser(u);

        return postService.createPost(p);
    }

    @GetMapping
    public List<Post> getPosts(@RequestParam String userId) {
        if (userId.isEmpty()) {
            return new ArrayList<>();
        }

        if (!userService.userExists(userId)) {
            return new ArrayList<>();
        }

        return postService.getPosts(userId);
    }

    @DeleteMapping
    public boolean deletePost(@RequestParam String postId, @RequestParam String userId) {
        if (userId.isEmpty() || postId.isEmpty()) {
            return false;
        }

        if (!userService.userExists(userId) || !postService.postExists(postId)) {
            return false;
        }

        Post p = postService.retrievePostById(postId);

        if (!(p.getUser().getId().equals(userId))) {
            return false;
        }

        return postService.deletePost(postId);
    }
}
