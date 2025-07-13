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

import example.flashchat.models.User;
import example.flashchat.security.JWTUtil;
import example.flashchat.services.UserService;

public class AuthControllerTest {
    
    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

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
    public void testCreateUser() {
        UserRequest userRequest = new UserRequest();
        userRequest.username = testUser.getUsername();
        userRequest.handle = testUser.getHandle();
        userRequest.password = testUser.getPassword();
        when(userService.userExists(testUser.getUsername())).thenReturn(false);

        // Any user object since id changes for each new user.
        when(userService.createUser(any(User.class))).thenReturn(true);
        when(jwtUtil.generateToken(userRequest.username)).thenReturn("A random JWT token");
        assertTrue(authController.createUser(userRequest));
    }

    @Test
    public void testCreateUserAlreadyExists() {
        UserRequest userRequest = new UserRequest();
        userRequest.username = testUser.getUsername();
        userRequest.handle = testUser.getHandle();
        userRequest.password = testUser.getPassword();
        when(userService.userExists(testUser.getUsername())).thenReturn(true);
        assertFalse(authController.createUser(userRequest));
    }

    @Test
    public void testCreateUserEmptyUsername() {
        UserRequest userRequest = new UserRequest();
        userRequest.username = "";
        userRequest.handle = testUser.getHandle();
        userRequest.password = testUser.getPassword();
        assertFalse(authController.createUser(userRequest));   
    }

    @Test
    public void testCreateUserEmptyPassword() {
        UserRequest userRequest = new UserRequest();
        userRequest.username = testUser.getUsername();
        userRequest.handle = testUser.getHandle();
        userRequest.password = "";
        assertFalse(authController.createUser(userRequest));
    }

    @Test
    public void testLogin() {
        LoginRequest request = new LoginRequest();
        request.username = testUser.getUsername();
        request.password = testUser.getPassword();

        when(userService.login(testUser.getUsername(), testUser.getPassword())).thenReturn(true);

        assertTrue(authController.login(request).getBody());
    }

    @Test
    public void testLoginIncorrect() {
        LoginRequest request = new LoginRequest();
        request.username = testUser.getUsername();
        request.password = testUser.getPassword();

        when(userService.login(testUser.getUsername(), testUser.getPassword())).thenReturn(false);
        assertFalse(authController.login(request).getBody());
    }
}

