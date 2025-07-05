package example.flashchat.controllers;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import example.flashchat.models.Post;
import example.flashchat.models.User;
import example.flashchat.services.PostService;
import example.flashchat.services.UserService;

public class PostControllerTest {
    
    @Mock
    private PostService postService;

    @Mock
    private UserService userService;

    @InjectMocks
    private PostController postController;

    private User testUser;
    private Post testPost;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");

        testPost = new Post();
        testPost.setUser(testUser);
        testPost.setPost("test post");
    }

    private PostRequest createPostRequest(String userId, String post) {
        PostRequest request = new PostRequest();
        request.userId = userId;
        request.post = post;
        return request;
    }

    @Test
    public void testCreatePost() {
        when(userService.userExists(testUser.getId())).thenReturn(true);
        when(postService.createPost(any(Post.class))).thenReturn(true);
        assertTrue(postController.createPost(createPostRequest(testUser.getId(), "test post")));
    }

    @Test
    public void testCreatePostEmptyUserId() {
        assertFalse(postController.createPost(createPostRequest("", "test post")));
    }

    @Test
    public void testCreatePostInvalidUserId() {
        when(userService.userExists(testUser.getId())).thenReturn(false);
        assertFalse(postController.createPost(createPostRequest(testUser.getId(), "test post")));
    }

    @Test
    public void testCreatePostEmptyPost() {
        when(userService.userExists(testUser.getId())).thenReturn(true);
        assertFalse(postController.createPost(createPostRequest(testUser.getId(), "")));
    }

    @Test
    public void testGetPosts() {
        when(postService.getPosts(testUser.getId())).thenReturn(new ArrayList<>());
        assertTrue(postController.getPostsFromUser(testUser.getId()).isEmpty());
    }

    @Test
    public void testGetPostsInvalidUserId() {
        when(userService.userExists(testUser.getId())).thenReturn(false);
        assertTrue(postController.getPostsFromUser(testUser.getId()).isEmpty());
    }

    @Test
    public void testDeletePost() {
        when(postService.deletePost(testPost.getId())).thenReturn(true);
        when(userService.userExists(testUser.getId())).thenReturn(true);
        when(postService.postExists(testPost.getId())).thenReturn(true);
        when(postService.retrievePostById(testPost.getId())).thenReturn(testPost);
        assertTrue(postController.deletePost(testPost.getId(), testUser.getId()));
    }

    @Test
    public void testDeletePostInvalidUserId() {
        when(userService.userExists(testUser.getId())).thenReturn(false);
        assertFalse(postController.deletePost(testPost.getId(), testUser.getId()));
    }

    @Test
    public void testDeletePostInvalidPostId() {
        when(userService.userExists(testUser.getId())).thenReturn(true);
        when(postService.postExists(testPost.getId())).thenReturn(false);
        assertFalse(postController.deletePost(testPost.getId(), testUser.getId()));
    }

    @Test
    public void testDeletePostInvalidPostOwner() {
        when(userService.userExists(testUser.getId())).thenReturn(true);
        when(postService.postExists(testPost.getId())).thenReturn(true);
        when(postService.retrievePostById(testPost.getId())).thenReturn(testPost);
        assertFalse(postController.deletePost(testPost.getId(), testUser.getId() + "k"));
    }

    @Test
    public void testDeletePostWithEmptyUserId() {
        assertFalse(postController.deletePost(testPost.getId(), ""));
    }

    @Test
    public void testDeletePostWithEmptyPostId() {
        assertFalse(postController.deletePost("", testUser.getId()));
    }
}
