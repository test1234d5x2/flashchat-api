package example.flashchat.services;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import example.flashchat.models.Media;
import example.flashchat.models.Post;
import example.flashchat.models.User;
import example.flashchat.repositories.MediaRepo;

public class MediaServiceTest {
    @Mock
    private MediaRepo mediaRepo;

    @InjectMocks
    private MediaService mediaService;

    private Media testMedia;
    private Post testPost;
    private User testUser;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");

        testPost = new Post();
        testPost.setUser(testUser);
        testPost.setPost("test post");

        testMedia = new Media();
        testMedia.setPost(testPost);
        testMedia.setFilePath("test media url");
    }

    @Test
    public void testAddMedia() {
        when(mediaRepo.save(testMedia)).thenReturn(testMedia);
        assertTrue(mediaService.addMedia(testMedia));
    }

    @Test
    public void testDeleteMedia() {
        when(mediaRepo.existsById(testMedia.getId())).thenReturn(true);
        assertTrue(mediaService.deleteMedia(testMedia.getId()));
    }

    @Test
    public void testMediaExists() {
        when(mediaRepo.existsById(testMedia.getId())).thenReturn(true);
        assertTrue(mediaService.mediaExists(testMedia.getId()));
    }

    @Test
    public void testGetMedia() {
        when(mediaRepo.findById(testMedia.getId())).thenReturn(Optional.of(testMedia));
        assertTrue(mediaService.getMedia(testMedia.getId()) != null);
    }
}
