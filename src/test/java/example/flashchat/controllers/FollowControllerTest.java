package example.flashchat.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import example.flashchat.models.Follow;
import example.flashchat.models.User;
import example.flashchat.services.FollowService;
import example.flashchat.services.UserService;

public class FollowControllerTest {
    @Mock
    private FollowService followService;

    @Mock
    private UserService userService;

    @InjectMocks
    private FollowController followController;

    private User testUser;
    private User testUser2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");

        testUser2 = new User();
        testUser2.setUsername("testuser2");
        testUser2.setPassword("password2");
    }

    @Test
    public void testFollowUser() {
        when(userService.userExists(testUser.getUsername())).thenReturn(true);
        when(userService.userExists(testUser2.getUsername())).thenReturn(true);
        when(userService.findById(testUser.getUsername())).thenReturn(testUser);
        when(userService.findById(testUser2.getUsername())).thenReturn(testUser2);
        when(followService.followExists(testUser, testUser2)).thenReturn(false);
        when(followService.addFollow(any(Follow.class))).thenReturn(true);
        boolean result = followController.follow(testUser.getUsername(), testUser2.getUsername());
        assertTrue(result);
    }

    @Test
    public void testFollowUserAlreadyFollowed() {
        when(userService.userExists(testUser.getUsername())).thenReturn(true);
        when(userService.userExists(testUser2.getUsername())).thenReturn(true);
        when(userService.findById(testUser.getUsername())).thenReturn(testUser);
        when(userService.findById(testUser2.getUsername())).thenReturn(testUser2);
        when(followService.followExists(testUser, testUser2)).thenReturn(true);
        boolean result = followController.follow(testUser.getUsername(), testUser2.getUsername());
        assertFalse(result);
    }

    @Test
    public void testFollowUserNotExists() {
        when(userService.userExists(testUser.getUsername())).thenReturn(false);
        when(userService.userExists(testUser2.getUsername())).thenReturn(true);
        boolean result = followController.follow(testUser.getUsername(), testUser2.getUsername());
        assertFalse(result);
    }

    @Test
    public void testFollowUserNotExists2() {
        when(userService.userExists(testUser.getUsername())).thenReturn(true);
        when(userService.userExists(testUser2.getUsername())).thenReturn(false);
        boolean result = followController.follow(testUser.getUsername(), testUser2.getUsername());
        assertFalse(result);
    }

    @Test
    public void testFollowUserEmpty() {
        boolean result = followController.follow("", "");
        assertFalse(result);
    }

    @Test
    public void testFollowUserEmpty2() {
        boolean result = followController.follow(testUser.getUsername(), "");
        assertFalse(result);
    }

    @Test
    public void testRemoveFollow() {
        when(userService.userExists(testUser.getUsername())).thenReturn(true);
        when(userService.userExists(testUser2.getUsername())).thenReturn(true);
        when(userService.findById(testUser.getUsername())).thenReturn(testUser);
        when(userService.findById(testUser2.getUsername())).thenReturn(testUser2);
        when(followService.followExists(testUser, testUser2)).thenReturn(true);
        when(followService.removeFollow(testUser, testUser2)).thenReturn(true);
        boolean result = followController.removeFollow(testUser.getUsername(), testUser2.getUsername());
        assertTrue(result);
    }

    @Test
    public void testRemoveFollowNotExists() {
        when(userService.userExists(testUser.getUsername())).thenReturn(true);
        when(userService.userExists(testUser2.getUsername())).thenReturn(false);
        boolean result = followController.removeFollow(testUser.getUsername(), testUser2.getUsername());
        assertFalse(result);
    }

    @Test
    public void testRemoveFollowUserNotExists() {
        when(userService.userExists(testUser.getUsername())).thenReturn(false);
        when(userService.userExists(testUser2.getUsername())).thenReturn(true);
        boolean result = followController.removeFollow(testUser.getUsername(), testUser2.getUsername());
        assertFalse(result);
    }

    @Test
    public void testRemoveFollowUserNotExists2() {
        when(userService.userExists(testUser.getUsername())).thenReturn(true);
        when(userService.userExists(testUser2.getUsername())).thenReturn(false);
        boolean result = followController.removeFollow(testUser.getUsername(), testUser2.getUsername());
        assertFalse(result);
    }

    @Test
    public void testRemoveFollowEmpty() {
        boolean result = followController.removeFollow("", "");
        assertFalse(result);
    }

    @Test
    public void testRemoveFollowEmpty2() {  
        boolean result = followController.removeFollow(testUser.getUsername(), "");
        assertFalse(result);
    }

    @Test
    public void testGetFollowers() {
        when(userService.userExists(testUser.getUsername())).thenReturn(true);
        when(userService.findById(testUser.getUsername())).thenReturn(testUser);
        List<Follow> result = followController.getFollowers(testUser.getUsername());
        assertEquals(testUser.getFollowers(), result);
    }

    @Test
    public void testGetFollowersNotExists() {
        when(userService.userExists(testUser.getUsername())).thenReturn(false);
        List<Follow> result = followController.getFollowers(testUser.getUsername());
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    public void testGetFollowersEmptyUserId() {
        List<Follow> result = followController.getFollowers("");
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    public void testGetFollowing() {
        when(userService.userExists(testUser.getUsername())).thenReturn(true);
        when(userService.findById(testUser.getUsername())).thenReturn(testUser);
        List<Follow> result = followController.getFollowing(testUser.getUsername());
        assertEquals(testUser.getFollowedBy(), result);
    }

    @Test
    public void testGetFollowingNotExists() {
        when(userService.userExists(testUser.getUsername())).thenReturn(false);
        List<Follow> result = followController.getFollowing(testUser.getUsername());
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    public void testGetFollowingEmptyUserId() {
        List<Follow> result = followController.getFollowing("");
        assertEquals(new ArrayList<>(), result);
    }
}
