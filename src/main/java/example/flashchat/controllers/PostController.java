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
    public boolean createPost(@RequestBody PostRequest request) {
        String userId = request.userId;
        String post = request.post;

        if (userId.isEmpty() || post.isEmpty()) {
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

    @GetMapping("/post/{postId}")
    public Post getPost(@PathVariable String postId) {
        if (postId.isEmpty()) {
            return null;
        }

        if (!postService.postExists(postId)) {
            return null;
        }

        return postService.retrievePostById(postId);
    }

    @GetMapping("/user/{userId}")
    public List<Post> getPosts(@PathVariable String userId) {
        if (userId.isEmpty()) {
            return new ArrayList<>();
        }

        if (!userService.userExists(userId)) {
            return new ArrayList<>();
        }

        return postService.getPosts(userId);
    }

    @GetMapping("/feed/{userId}")
    public List<Post> getFeed(@PathVariable String userId) {
        if (userId.isEmpty()) {
            return new ArrayList<>();
        }

        if (!userService.userExists(userId)) {
            return new ArrayList<>();
        }

        return postService.allPosts();
    } // TODO: Implement recommendation servicelogic.

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


class PostRequest {
    public String userId;
    public String post;
}