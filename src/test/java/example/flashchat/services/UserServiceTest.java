package example.flashchat.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import example.flashchat.repositories.UserRepo;
import example.flashchat.models.LoginDetails;
import example.flashchat.models.User;

public class UserServiceTest {
    
    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

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
        User newUser = new User();
        newUser.setUsername("testuser4");
        newUser.setPassword("password4");

        when(userRepo.findByUsername(newUser.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(newUser.getPassword())).thenReturn("password4");
        when(userRepo.save(newUser)).thenReturn(newUser);
        
        boolean result = userService.createUser(newUser);
        assertTrue(result);
        verify(userRepo, times(1)).save(newUser);
    }

    @Test 
    public void testLoginCorrect() {
        when(passwordEncoder.encode(testUser.getPassword())).thenReturn(testUser.getPassword());
        when(passwordEncoder.matches(testUser.getPassword(), testUser.getPassword())).thenReturn(true);
        userService.createUser(testUser);

        LoginDetails loginDetails = new LoginDetails();
        loginDetails.setUsername(testUser.getUsername());
        loginDetails.setPassword(testUser.getPassword());

        when(userRepo.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

        boolean result = userService.login(loginDetails.getUsername(), loginDetails.getPassword());
        assertTrue(result);
    }

    @Test
    public void testLoginIncorrect() {

        User testUser2 = new User();
        testUser2.setUsername("testuser");
        testUser2.setPassword("password2");

        when(userRepo.findByUsername(testUser.getUsername())).thenReturn(Optional.empty());
        boolean result = userService.login(testUser2.getUsername(), testUser2.getPassword());
        assertFalse(result);
    }

    @Test
    public void testDeleteUser() {
        boolean result = userService.deleteUser(testUser);
        assertTrue(result);
    }

    @Test
    public void testUserExists() {
        when(userRepo.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        boolean result = userService.userExists(testUser.getId());
        assertTrue(result);
    }

    @Test
    public void testUserExistsNotFound() {
        when(userRepo.findById(testUser.getId())).thenReturn(Optional.empty());
        boolean result = userService.userExists(testUser.getId());
        assertFalse(result);
    }

    @Test
    public void testFindById() {
        when(userRepo.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        User result = userService.findById(testUser.getId());
        assertEquals(testUser, result);
    }

    @Test
    public void testFindByIdNotFound() {
        when(userRepo.findById(testUser.getId())).thenReturn(Optional.empty());
        User result = userService.findById(testUser.getId());
        assertNull(result);
    }

    @Test
    public void testSearchUsers_PartialMatch() {
        User user1 = new User();
        user1.setUsername("alice");
        User user2 = new User();
        user2.setUsername("alicia");
        User user3 = new User();
        user3.setUsername("bob");

        String searchQuery = "ali";
        List<User> expected = List.of(user1, user2);
        when(userRepo.findByUsernameContaining(searchQuery)).thenReturn(expected);

        List<User> result = userService.searchUsers(searchQuery);
        assertEquals(2, result.size());
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));
        assertFalse(result.contains(user3));
    }

    @Test
    public void testSearchUsers_NoMatch() {
        String searchQuery = "xyz";
        when(userRepo.findByUsernameContaining(searchQuery)).thenReturn(List.of());
        List<User> result = userService.searchUsers(searchQuery);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSearchUsers_MultipleMatches() {
        User user1 = new User();
        user1.setUsername("johnny");
        User user2 = new User();
        user2.setUsername("john");
        User user3 = new User();
        user3.setUsername("jonathan");

        String searchQuery = "john";
        List<User> expected = List.of(user1, user2);
        when(userRepo.findByUsernameContaining(searchQuery)).thenReturn(expected);

        List<User> result = userService.searchUsers(searchQuery);
        assertEquals(2, result.size());
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));
        assertFalse(result.contains(user3));
    }
}
