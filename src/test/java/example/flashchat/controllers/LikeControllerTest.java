package example.flashchat.controllers;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import example.flashchat.models.Like;
import example.flashchat.models.Post;
import example.flashchat.models.User;
import example.flashchat.services.NotificationService;
import example.flashchat.models.Notification;
import example.flashchat.services.LikeService;
import example.flashchat.services.PostService;
import example.flashchat.services.UserService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;

public class LikeControllerTest {
    @Mock
    private LikeService likeService;

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private LikeController likeController;

    private User testUser;
    private User testUser2;
    private Post testPost;

    private final String AUTHENTICATED_USER_USERNAME = "testuser2";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");

        testUser2 = new User();
        testUser2.setUsername(AUTHENTICATED_USER_USERNAME);
        testUser2.setPassword("password2");

        testPost = new Post();
        testPost.setUser(testUser);
        testPost.setPost("test post");
    }

    private LikeRequest createLikeRequest(String postId) {
        LikeRequest request = new LikeRequest();
        request.postId = postId;
        return request;
    }

    private Authentication createMockAuthentication(String username, boolean authenticated) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        authentication.setAuthenticated(authenticated);
        return authentication;
    }


    @Test
    public void testAddLike() {
        when(postService.postExists(testPost.getId())).thenReturn(true);
        when(userService.userExists(testUser2.getId())).thenReturn(true);
        when(userService.userExistsByUsername(testUser2.getUsername())).thenReturn(true);
        when(userService.findById(testUser2.getId())).thenReturn(testUser2);
        when(userService.findByUsername(testUser2.getUsername())).thenReturn(testUser2);
        when(likeService.likeExists(testPost, testUser2)).thenReturn(false);
        when(postService.retrievePostById(testPost.getId())).thenReturn(testPost);
        when(likeService.addLike(any(Like.class))).thenReturn(true);
        when(notificationService.createNotification(any(Notification.class))).thenReturn(true);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        assertTrue(likeController.addLike(authentication, createLikeRequest(testPost.getId())));
    }

    @Test
    public void testLikeAlreadyExists() {
        when(likeService.likeExists(testPost, testUser2)).thenReturn(true);
        
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        assertFalse(likeController.addLike(authentication, createLikeRequest(testPost.getId())));
    }

    @Test
    public void testUserLikesOwnPost() {
        when(postService.postExists(testPost.getId())).thenReturn(true);
        when(userService.userExists(testUser.getId())).thenReturn(true);
        when(userService.userExistsByUsername(testUser.getUsername())).thenReturn(true);
        when(postService.retrievePostById(testPost.getId())).thenReturn(testPost);
        when(userService.findById(testUser.getId())).thenReturn(testUser);
        when(userService.findByUsername(testUser.getUsername())).thenReturn(testUser);
        when(likeService.likeExists(testPost, testUser)).thenReturn(false);

        Authentication authentication = createMockAuthentication("testuser", false);

        assertFalse(likeController.addLike(authentication, createLikeRequest(testPost.getId())));
    }

    @Test
    public void testAddLikeInvalidPostId() {
        when(postService.postExists(testPost.getId())).thenReturn(false);
        
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        assertFalse(likeController.addLike(authentication, createLikeRequest(testPost.getId())));
    }

    @Test
    public void testEmptyPostId() {
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);
        assertFalse(likeController.addLike(authentication, createLikeRequest("")));
    }

    @Test
    public void testDeleteLike() {
        when(postService.postExists(testPost.getId())).thenReturn(true);
        when(userService.userExists(testUser2.getId())).thenReturn(true);
        when(userService.userExistsByUsername(testUser2.getUsername())).thenReturn(true);
        when(userService.findById(testUser2.getId())).thenReturn(testUser2);
        when(userService.findByUsername(testUser2.getUsername())).thenReturn(testUser2);
        when(postService.retrievePostById(testPost.getId())).thenReturn(testPost);
        when(likeService.likeExists(testPost, testUser2)).thenReturn(true);
        when(likeService.deleteLike(testPost, testUser2)).thenReturn(true);
        
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        assertTrue(likeController.deleteLike(authentication, createLikeRequest(testPost.getId())));
    }

    @Test
    public void testDeleteLikeInvalidLike() {
        when(likeService.likeExists(testPost, testUser2)).thenReturn(false);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        assertFalse(likeController.deleteLike(authentication, createLikeRequest(testPost.getId())));
    }

    @Test
    public void testDeleteLikeInvalidPostId() {
        when(postService.postExists(testPost.getId())).thenReturn(false);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        assertFalse(likeController.deleteLike(authentication, createLikeRequest(testPost.getId())));
    }

    @Test
    public void testDeleteLikeEmptyPostId() {
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);
        assertFalse(likeController.deleteLike(authentication, createLikeRequest("")));
    }

    @Test
    public void testGetLikedPosts() {
        when(userService.findById(testUser2.getId())).thenReturn(testUser2);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);
        assertTrue(likeController.getLikedPosts(authentication).isEmpty());
    }

    @Test
    public void testGetLikedPostsInvalidUserId() {
        when(userService.userExistsByUsername(testUser2.getUsername())).thenReturn(false);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);
        assertTrue(likeController.getLikedPosts(authentication).isEmpty());
    }

    @Test
    public void testCheckLike_BothIdsEmpty() {
        LikeRequest request = createLikeRequest("");
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);
        assertFalse(likeController.checkLike(authentication, request));
    }

    @Test
    public void testCheckLike_PostDoesNotExist() {
        LikeRequest request = createLikeRequest("postId");
        when(postService.postExists("postId")).thenReturn(false);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);
        assertFalse(likeController.checkLike(authentication, request));
    }

    @Test
    public void testCheckLike_LikeExists() {
        LikeRequest request = createLikeRequest("postId");
        when(postService.postExists("postId")).thenReturn(true);
        when(userService.userExistsByUsername(AUTHENTICATED_USER_USERNAME)).thenReturn(true);
        Post post = new Post();
        User user = new User();
        when(postService.retrievePostById("postId")).thenReturn(post);
        when(userService.findByUsername(AUTHENTICATED_USER_USERNAME)).thenReturn(user);
        when(likeService.likeExists(post, user)).thenReturn(true);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);
        assertTrue(likeController.checkLike(authentication, request));
    }

    @Test
    public void testCheckLike_LikeDoesNotExist() {
        LikeRequest request = createLikeRequest("postId");
        when(postService.postExists("postId")).thenReturn(true);
        when(userService.userExistsByUsername(AUTHENTICATED_USER_USERNAME)).thenReturn(true);
        Post post = new Post();
        User user = new User();
        when(postService.retrievePostById("postId")).thenReturn(post);
        when(userService.findByUsername(AUTHENTICATED_USER_USERNAME)).thenReturn(user);
        when(likeService.likeExists(post, user)).thenReturn(false);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);
        assertFalse(likeController.checkLike(authentication, request));
    }
}