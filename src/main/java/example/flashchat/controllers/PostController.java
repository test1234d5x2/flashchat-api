package example.flashchat.controllers;

import example.flashchat.Utils;
import example.flashchat.models.Media;
import example.flashchat.models.Post;
import example.flashchat.models.User;
import example.flashchat.services.MediaService;
import example.flashchat.services.PostRecommendationService;
import example.flashchat.services.PostService;
import example.flashchat.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRecommendationService postRecommendationService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private UserService userService;

    @PostMapping
    public boolean createPost(@RequestParam String userId, @RequestParam String post, @RequestParam(required = false) MultipartFile[] images) {
        System.out.println("Creating post for user: " + userId);
        System.out.println("Post content: " + post);
        System.out.println("Number of images: " + (images != null ? images.length : 0));

        if (userId.isEmpty() || post.isEmpty()) {
            return false;
        }

        if (!userService.userExists(userId)) {
            return false;
        }

        User u = userService.findById(userId);
        Post p = new Post();
        p.setPost(post);
        p.setUser(u);

        boolean postCreated = postService.createPost(p);

        // If post was created successfully and images were provided, handle the images
        if (postCreated && images != null && images.length > 0) {
            for (MultipartFile image : images) {

                String filePath = Utils.getFilePath(image.getOriginalFilename());

                Media media = new Media();
                media.setPost(p);
                media.setFilePath(filePath);

                if (mediaService.addMedia(media)) {
                    // Save file to disk
                    try {
                        image.transferTo(new File(filePath));
                    } catch (IOException e) {
                        mediaService.deleteMedia(media.getId());
                        e.printStackTrace();

                        return false;
                    }
                } else {
                    return false;
                }
            }
        }

        return postCreated;
    }

    @GetMapping("/post/{postId}")
    public Post getPost(@PathVariable String postId) {
        if (postId.isEmpty()) {
            return null;
        }

        if (!postService.postExists(postId)) {
            return null;
        }

        Post p = postService.retrievePostById(postId);
        postService.incrementViews(p);
        return p;
    }

    @GetMapping("/user/{userId}/{page}")
    public List<Post> getPostsFromUser(@PathVariable String userId, @PathVariable int page) {
        if (userId.isEmpty()) {
            return new ArrayList<>();
        }

        if (!userService.userExists(userId)) {
            return new ArrayList<>();
        }

        List<Post> posts = postService.getPosts(userId);

        int pageSize = 20;
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, posts.size());
        if (fromIndex >= posts.size() || fromIndex < 0) {
            return new ArrayList<>();
        }
        List<Post> pagedPosts = posts.subList(fromIndex, toIndex);
        incrementViews(pagedPosts);
        return pagedPosts;
    }

    @GetMapping("/feed/{userId}/{page}")
    public List<Post> getFeed(@PathVariable String userId, @PathVariable int page) {
        if (userId.isEmpty()) {
            return new ArrayList<>();
        }

        if (!userService.userExists(userId)) {
            return new ArrayList<>();
        }

        User user = userService.findById(userId);
        List<Post> posts = postRecommendationService.getRecommendedPosts(user);
        // Pagination logic: 20 posts per page
        int pageSize = 20;
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, posts.size());
        if (fromIndex >= posts.size() || fromIndex < 0) {
            return new ArrayList<>();
        }
        List<Post> pagedPosts = posts.subList(fromIndex, toIndex);
        incrementViews(pagedPosts);
        return pagedPosts;
    } // TODO: Testing Required.

    @GetMapping("/following/{userId}/{page}")
    public List<Post> getFollowingPosts(@PathVariable String userId, @PathVariable int page) {
        if (userId.isEmpty()) {
            return new ArrayList<>();
        }

        if (!userService.userExists(userId)) {
            return new ArrayList<>();
        }

        User user = userService.findById(userId);
        List<Post> posts = new ArrayList<>();

        user.getFollowing().forEach(follow -> {
            List<Post> followingPosts = postService.getPosts(follow.getFollowed().getId());
            posts.addAll(followingPosts);
        });

        // Sort posts by creation date (newest first)
        posts.sort((p1, p2) -> p2.getDatePosted().compareTo(p1.getDatePosted()));

        // Pagination logic: 20 posts per page
        int pageSize = 20;
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, posts.size());
        if (fromIndex >= posts.size() || fromIndex < 0) {
            return new ArrayList<>();
        }
        List<Post> pagedPosts = posts.subList(fromIndex, toIndex);
        incrementViews(pagedPosts);
        return pagedPosts;
    } // TODO: Testing Required.

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

    private void incrementViews(List<Post> posts) {
        posts.parallelStream().forEach(postService::incrementViews);
    }
}