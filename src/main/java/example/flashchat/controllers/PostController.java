package example.flashchat.controllers;

import example.flashchat.models.Post;
import example.flashchat.models.User;
import example.flashchat.requestStructures.IdRequest;
import example.flashchat.requestStructures.PostAndUserRequest;
import example.flashchat.requestStructures.PostRequest;
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
    public boolean createPost(@RequestBody PostRequest post) {
        String user_id = post.user_id;
        if (user_id.isEmpty()) {
            return false;
        }

        if (userService.userExists(user_id)) {
            return false;
        }

        User u = userService.findById(user_id);
        Post p = new Post();
        p.setPost(post.post);
        p.setUser(u);

        return postService.createPost(p);
    }

    @GetMapping
    public List<Post> getPosts(@RequestBody IdRequest idRequest) {
        String user_id = idRequest.id;
        if (user_id.isEmpty()) {
            return new ArrayList<>();
        }

        if (userService.userExists(user_id)) {
            return new ArrayList<>();
        }

        return postService.getPosts(user_id);
    }

    @DeleteMapping
    public boolean deletePost(@RequestBody PostAndUserRequest postAndUserRequest) {
        String post_id = postAndUserRequest.post_id;
        String user_id = postAndUserRequest.user_id;

        if (user_id.isEmpty() || post_id.isEmpty()) {
            return false;
        }

        if (userService.userExists(user_id) || !postService.postExists(post_id)) {
            return false;
        }

        Post p = postService.retrievePostById(post_id);

        if (!(p.getUser().getId().equals(user_id))) {
            return false;
        }

        return postService.deletePost(post_id);
    }
}
