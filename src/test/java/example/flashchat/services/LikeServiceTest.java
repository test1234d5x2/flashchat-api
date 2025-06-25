package example.flashchat.services;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import example.flashchat.models.Like;
import example.flashchat.models.Post;
import example.flashchat.models.User;
import example.flashchat.repositories.LikeRepo;

public class LikeServiceTest {
    @Mock
    private LikeRepo likeRepo;

    @InjectMocks
    private LikeService likeService;
    
    private Like testLike;
    private User testUser;
    private Post testPost;
    
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");

        testPost = new Post();
        testPost.setUser(testUser);
        testPost.setPost("test post");

        testLike = new Like();
        testLike.setLikedBy(testUser);
        testLike.setPostLiked(testPost);
    }

    @Test
    public void testAddLike() {
        when(likeRepo.save(testLike)).thenReturn(testLike);
        assertTrue(likeService.addLike(testLike));
    }

    @Test
    public void testDeleteLike() {
        when(likeRepo.findByPostLikedAndLikedBy(testPost, testUser)).thenReturn(Optional.of(testLike));
        assertTrue(likeService.deleteLike(testPost, testUser));
    }

    @Test
    public void testLikeExists() {
        when(likeRepo.findByPostLikedAndLikedBy(testPost, testUser)).thenReturn(Optional.of(testLike));
        assertTrue(likeService.likeExists(testPost, testUser));
    }
    
}
