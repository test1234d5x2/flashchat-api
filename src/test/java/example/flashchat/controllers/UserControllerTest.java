package example.flashchat.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    private final String AUTHENTICATED_USER_USERNAME = "testuser";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setUsername(AUTHENTICATED_USER_USERNAME);
        testUser.setHandle("testhandle");
        testUser.setPassword("password");
    }

    private Authentication createMockAuthentication(String username, boolean authenticated) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        authentication.setAuthenticated(authenticated);
        return authentication;
    }


    @Test
    public void testDeleteUser() {
        when(userService.deleteUser(testUser.getId())).thenReturn(true);
        assertTrue(userController.deleteUser(testUser.getId()));
    }

    @Test
    public void testGetUserDetails_ValidId() {
        User user = new User();
        user.setUsername("alice");

        when(userService.userExists(user.getId())).thenReturn(true);
        when(userService.findById(user.getId())).thenReturn(user);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        User result = userController.getUserDetails(authentication, user.getId());
        assertTrue(result.equals(user));
    }

    @Test
    public void testGetUserDetails_EmptyId() {
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);
        User result = userController.getUserDetails(authentication, "");
        assertTrue(result == null);
    }

    @Test
    public void testGetUserDetails_NotFound() {
        String userId = "notfound";
        when(userService.userExists(userId)).thenReturn(false);
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);
        User result = userController.getUserDetails(authentication, userId);
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
