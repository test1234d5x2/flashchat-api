package example.flashchat.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import example.flashchat.models.Follow;
import example.flashchat.models.User;
import example.flashchat.repositories.FollowRepo;

public class FollowServiceTest {

    @Mock
    private FollowRepo followRepo;

    @InjectMocks
    private FollowService followService;

    private Follow testFollow;
    private User testUser;
    private User testUser2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");

        User testUser2 = new User();
        testUser2.setUsername("testuser2");
        testUser2.setPassword("password2");

        testFollow = new Follow();
        testFollow.setFollower(testUser);
        testFollow.setFollowed(testUser2);
    }

    @Test
    public void testAddFollow() {
        when(followRepo.save(testFollow)).thenReturn(testFollow);
        assertTrue(followService.addFollow(testFollow));
    }

    @Test
    public void testRemoveFollow() {
        when(followRepo.findByFollowerAndFollowed(testUser, testUser2)).thenReturn(Optional.of(testFollow));
        assertTrue(followService.removeFollow(testUser, testUser2));
    }

    @Test
    public void testFollowExists() {
        when(followRepo.findByFollowerAndFollowed(testUser, testUser2)).thenReturn(Optional.of(testFollow));
        assertTrue(followService.followExists(testUser, testUser2));
    }

    @Test
    public void testFollowDoesNotExist() {
        when(followRepo.findByFollowerAndFollowed(testUser, testUser2)).thenReturn(Optional.empty());
        assertFalse(followService.followExists(testUser, testUser2));
    }
}
