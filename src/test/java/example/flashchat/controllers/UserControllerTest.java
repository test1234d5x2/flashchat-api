package example.flashchat.controllers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import example.flashchat.models.LoginDetails;
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
        testUser.setPassword("password");
    }


    @Test
    public void testCreateUser() {
        LoginDetails loginDetails = new LoginDetails();
        loginDetails.setUsername(testUser.getUsername());
        loginDetails.setPassword(testUser.getPassword());
        when(userService.userExists(testUser.getUsername())).thenReturn(false);

        // Any user object since id changes for each new user.
        when(userService.createUser(any(User.class))).thenReturn(true);
        assertTrue(userController.createUser(loginDetails));
    }

    @Test
    public void testCreateUserAlreadyExists() {
        when(userService.userExists(testUser.getUsername())).thenReturn(true);
        assertFalse(userController.createUser(testUser));
    }

    @Test
    public void testCreateUserEmptyUsername() {
        testUser.setUsername("");
        assertFalse(userController.createUser(testUser));   
    }

    @Test
    public void testCreateUserEmptyPassword() {
        testUser.setPassword("");
        assertFalse(userController.createUser(testUser));
    }

    @Test
    public void testLogin() {
        when(userService.login(testUser)).thenReturn(true);
        assertTrue(userController.login(testUser));
    }

    @Test
    public void testLoginIncorrect() {
        when(userService.login(testUser)).thenReturn(false);
        assertFalse(userController.login(testUser));
    }

    @Test
    public void testDeleteUser() {
        when(userService.deleteUser(testUser.getId())).thenReturn(true);
        assertTrue(userController.deleteUser(testUser.getId()));
    }    
}
