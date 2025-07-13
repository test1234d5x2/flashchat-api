package example.flashchat.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import example.flashchat.models.User;
import example.flashchat.services.UserService;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController userController;

    private User testUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setHandle("testhandle");
        testUser.setPassword("password");
    }

    @Test
    public void testDeleteUser() {
        when(userService.deleteUser(testUser.getId())).thenReturn(true);
        assertTrue(userController.deleteUser(testUser.getId()));
    }

    @Test
    public void testGetUserDetails_ValidId() {
        String userId = "123";
        User user = new User();
        user.setUsername("alice");
        when(userService.userExists(userId)).thenReturn(true);
        when(userService.findById(userId)).thenReturn(user);
        User result = userController.getUserDetails(userId);
        assertTrue(result == user);
    }

    @Test
    public void testGetUserDetails_EmptyId() {
        User result = userController.getUserDetails("");
        assertTrue(result == null);
    }

    @Test
    public void testGetUserDetails_NotFound() {
        String userId = "notfound";
        when(userService.userExists(userId)).thenReturn(false);
        User result = userController.getUserDetails(userId);
        assertTrue(result == null);
    }

    @Test
    public void testSearchUsersGet_ValidQuery() {
        String searchQuery = "ali";
        User user1 = new User();
        user1.setUsername("alice");
        User user2 = new User();
        user2.setUsername("alicia");
        when(userService.searchUsers(searchQuery)).thenReturn(java.util.List.of(user1, user2));
        java.util.List<User> result = userController.searchUsersGet(searchQuery);
        assertTrue(result.size() == 2);
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));
    }

    @Test
    public void testSearchUsersGet_EmptyQuery() {
        java.util.List<User> result = userController.searchUsersGet("");
        assertTrue(result.isEmpty());
    }
}
