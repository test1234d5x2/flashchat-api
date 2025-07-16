package example.flashchat.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import example.flashchat.models.Follow;
import example.flashchat.models.User;
import example.flashchat.services.FollowService;
import example.flashchat.services.UserService;
import example.flashchat.services.NotificationService;
import example.flashchat.models.Notification;

public class FollowControllerTest {
    @Mock
    private FollowService followService;

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private FollowController followController;

    private User testUser;
    private User testUser2;

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
    }

    private FollowRequest createFollowRequest(String followedId) {
        FollowRequest request = new FollowRequest();
        request.followedId = followedId;
        return request;
    }

    private Authentication createMockAuthentication(String username, boolean authenticated) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        authentication.setAuthenticated(authenticated);
        return authentication;
    }


    @Test
    public void testFollowUser() {
        when(userService.userExists(testUser.getId())).thenReturn(true);
        when(userService.userExists(testUser2.getId())).thenReturn(true);
        when(userService.userExistsByUsername(testUser2.getUsername())).thenReturn(true);
        when(userService.findById(testUser.getId())).thenReturn(testUser);
        when(userService.findById(testUser2.getId())).thenReturn(testUser2);
        when(userService.findByUsername(testUser2.getUsername())).thenReturn(testUser2);
        when(followService.followExists(testUser, testUser2)).thenReturn(false);
        when(followService.addFollow(any(Follow.class))).thenReturn(true);
        when(notificationService.createNotification(any(Notification.class))).thenReturn(true);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        boolean result = followController.follow(authentication, createFollowRequest(testUser.getId()));
        assertTrue(result);
    }

    @Test
    public void testFollowUserAlreadyFollowed() {
        when(userService.userExists(testUser.getUsername())).thenReturn(true);
        when(userService.userExists(testUser2.getUsername())).thenReturn(true);
        when(userService.userExistsByUsername(testUser2.getUsername())).thenReturn(true);
        when(userService.findById(testUser.getUsername())).thenReturn(testUser);
        when(userService.findById(testUser2.getUsername())).thenReturn(testUser2);
        when(userService.findByUsername(testUser2.getUsername())).thenReturn(testUser2);
        when(followService.followExists(testUser, testUser2)).thenReturn(true);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        boolean result = followController.follow(authentication, createFollowRequest(testUser.getId()));
        assertFalse(result);
    }

    @Test
    public void testFollowUserNotExists() {
        when(userService.userExists(testUser.getId())).thenReturn(false);
        when(userService.userExists(testUser2.getId())).thenReturn(true);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        boolean result = followController.follow(authentication, createFollowRequest(testUser.getId()));
        assertFalse(result);
    }

    @Test
    public void testFollowUserNotExists2() {
        when(userService.userExists(testUser.getUsername())).thenReturn(true);
        when(userService.userExists(testUser2.getUsername())).thenReturn(false);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        boolean result = followController.follow(authentication, createFollowRequest(testUser.getId()));
        assertFalse(result);
    }

    @Test
    public void testFollowUserEmpty() {
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        boolean result = followController.follow(authentication, createFollowRequest(""));
        assertFalse(result);
    }

    @Test
    public void testRemoveFollow() {
        when(userService.userExists(testUser.getId())).thenReturn(true);
        when(userService.userExists(testUser2.getId())).thenReturn(true);
        when(userService.userExistsByUsername(testUser2.getUsername())).thenReturn(true);
        when(userService.findById(testUser.getId())).thenReturn(testUser);
        when(userService.findById(testUser2.getId())).thenReturn(testUser2);
        when(userService.findByUsername(testUser2.getUsername())).thenReturn(testUser2);
        when(followService.followExists(testUser2, testUser)).thenReturn(true);
        when(followService.removeFollow(testUser2, testUser)).thenReturn(true);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        boolean result = followController.removeFollow(authentication, createFollowRequest(testUser.getId()));
        assertTrue(result);
    }

    @Test
    public void testRemoveFollowUserNotExists() {
        when(userService.userExists(testUser.getUsername())).thenReturn(false);
        when(userService.userExists(testUser2.getUsername())).thenReturn(true);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        boolean result = followController.removeFollow(authentication, createFollowRequest(testUser.getId()));
        assertFalse(result);
    }

    @Test
    public void testRemoveFollowEmpty() {
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        boolean result = followController.removeFollow(authentication, createFollowRequest(""));
        assertFalse(result);
    }

    @Test
    public void testGetFollowers() {
        when(userService.userExistsByUsername(testUser.getUsername())).thenReturn(true);
        when(userService.findByUsername(testUser.getUsername())).thenReturn(testUser);
        
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);
        List<Follow> result = followController.getFollowers(authentication);
        assertEquals(testUser.getFollowers(), result);
    }



    @Test
    public void testCheckFollowTrue() {
        when(userService.userExists(testUser.getId())).thenReturn(true);
        when(userService.userExists(testUser2.getId())).thenReturn(true);
        when(userService.userExistsByUsername(testUser2.getUsername())).thenReturn(true);
        when(userService.findById(testUser.getId())).thenReturn(testUser);
        when(userService.findById(testUser2.getId())).thenReturn(testUser2);
        when(userService.findByUsername(testUser2.getUsername())).thenReturn(testUser2);
        when(followService.followExists(testUser2, testUser)).thenReturn(true);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        boolean result = followController.checkFollow(authentication, createFollowRequest(testUser.getId()));
        assertTrue(result);
    }


    @Test
    public void testCheckFollowFalse() {
        when(userService.userExists(testUser.getId())).thenReturn(true);
        when(userService.userExists(testUser2.getId())).thenReturn(true);
        when(userService.userExistsByUsername(testUser2.getUsername())).thenReturn(true);
        when(userService.findById(testUser.getId())).thenReturn(testUser);
        when(userService.findById(testUser2.getId())).thenReturn(testUser2);
        when(userService.findByUsername(testUser2.getUsername())).thenReturn(testUser2);
        when(followService.followExists(testUser2, testUser)).thenReturn(false);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        boolean result = followController.checkFollow(authentication, createFollowRequest(testUser.getId()));
        assertFalse(result);
    }


    @Test
    public void testCheckFollowTheFollowerIdNotExists() {
        when(userService.userExists(testUser.getId())).thenReturn(false);
        when(userService.userExists(testUser2.getId())).thenReturn(true);
        when(userService.userExistsByUsername(testUser2.getUsername())).thenReturn(true);
        when(followService.followExists(testUser2, testUser)).thenReturn(false);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        boolean result = followController.checkFollow(authentication, createFollowRequest(testUser.getId()));
        assertFalse(result);
    }


    @Test
    public void testCheckFollowTheFollowerIdEmpty() {
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);
        boolean result = followController.checkFollow(authentication, createFollowRequest(""));
        assertFalse(result);
    }


    @Test
    public void testCheckFollowNoAuthentication() {
        boolean result = followController.checkFollow(null, createFollowRequest(""));
        assertFalse(result);
    }
}
