package example.flashchat.controllers;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;

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

    private final String AUTHENTICATED_USER_USERNAME = "testuser";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setUsername(AUTHENTICATED_USER_USERNAME);
        testUser.setPassword("password");

        testPost = new Post();
        testPost.setUser(testUser);
        testPost.setPost("test post");
    }

    private Authentication createMockAuthentication(String username, boolean authenticated) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        authentication.setAuthenticated(authenticated);
        return authentication;
    }

    @Test
    public void testCreatePost() {
        when(userService.userExistsByUsername(testUser.getUsername())).thenReturn(true);
        when(userService.findByUsername(testUser.getUsername())).thenReturn(testUser);
        when(postService.createPost(any(Post.class))).thenReturn(true);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        assertTrue(postController.createPost(authentication, "test post", null));
    }

    @Test
    public void testCreatePostNullAuthentication() {
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);
        assertFalse(postController.createPost(authentication, "test post", null));
    }

    @Test
    public void testCreatePostEmptyPost() {
        when(userService.userExistsByUsername(testUser.getUsername())).thenReturn(true);
        when(userService.findByUsername(testUser.getUsername())).thenReturn(testUser);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);
        assertFalse(postController.createPost(authentication, "", null));
    }

    @Test
    public void testDeletePost() {
        when(postService.deletePost(testPost.getId())).thenReturn(true);
        when(userService.userExistsByUsername(testUser.getUsername())).thenReturn(true);
        when(userService.findByUsername(testUser.getUsername())).thenReturn(testUser);
        when(postService.postExists(testPost.getId())).thenReturn(true);
        when(postService.retrievePostById(testPost.getId())).thenReturn(testPost);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);
        assertTrue(postController.deletePost(authentication, testPost.getId()));
    }

    @Test
    public void testDeletePostInvalidPostId() {
        when(postService.deletePost(testPost.getId())).thenReturn(true);
        when(userService.userExistsByUsername(testUser.getUsername())).thenReturn(true);
        when(userService.findByUsername(testUser.getUsername())).thenReturn(testUser);
        when(postService.postExists(testPost.getId())).thenReturn(false);
        when(postService.retrievePostById(testPost.getId())).thenReturn(testPost);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);
        assertFalse(postController.deletePost(authentication, testPost.getId()));
    }

    @Test
    public void testDeletePostInvalidPostOwner() {
        when(postService.deletePost(testPost.getId())).thenReturn(true);
        when(userService.userExistsByUsername(testUser.getUsername())).thenReturn(true);
        when(userService.findByUsername(testUser.getUsername())).thenReturn(testUser);
        when(postService.postExists(testPost.getId())).thenReturn(false);
        when(postService.retrievePostById(testPost.getId())).thenReturn(testPost);

        Authentication authentication = createMockAuthentication("not_the_owner", false);
        assertFalse(postController.deletePost(authentication, testPost.getId()));
    }

    @Test
    public void testDeletePostWithEmptyAuthentication() {
        assertFalse(postController.deletePost(null, testPost.getId()));
    }

    @Test
    public void testDeletePostWithEmptyPostId() {
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);
        assertFalse(postController.deletePost(authentication, ""));
    }


    @Test
    public void testGetPost() {
        when(postService.postExists(testPost.getId())).thenReturn(true);
        when(postService.retrievePostById(testPost.getId())).thenReturn(testPost);

        assertEquals(postController.getPost(testPost.getId()), testPost);
    }

    
    @Test
    public void testGetPostPostDoesNotExist() {
        when(postService.postExists(testPost.getId())).thenReturn(false);
        when(postService.retrievePostById(testPost.getId())).thenReturn(null);

        assertNull(postController.getPost(testPost.getId()));
    }

    @Test
    public void testGetPostEmptyPostId() {
        assertNull(postController.getPost(""));
    }
}
