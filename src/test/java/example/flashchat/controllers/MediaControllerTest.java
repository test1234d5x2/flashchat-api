package example.flashchat.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import example.flashchat.services.MediaService;
import example.flashchat.services.PostService;
import example.flashchat.Utils;
import example.flashchat.models.Media;
import example.flashchat.models.Post;
import example.flashchat.models.User;

public class MediaControllerTest {

    @Mock
    private MediaService mediaService;

    @Mock
    private PostService postService;

    @InjectMocks
    private MediaController mediaController;

    private Media testMedia;
    private Post testPost;
    private User testUser;

    private MockMultipartFile file = new MockMultipartFile(
        "file",
        "test.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "Hello, World!".getBytes()
    );
    
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
        testMedia.setFilePath("test/path");
    }

    @Test
    public void testAddMedia() {        
        testMedia.setFilePath(Utils.getFilePath(file.getName()));

        when(postService.postExists(testPost.getId())).thenReturn(true);
        when(postService.retrievePostById(testPost.getId())).thenReturn(testPost);
        when(mediaService.addMedia(any(Media.class))).thenReturn(true);

        assertTrue(mediaController.addMedia(testPost.getId(), file));
    }

    @Test
    public void testAddMediaFailed() {
        when(postService.postExists(testPost.getId())).thenReturn(true);
        when(postService.retrievePostById(testPost.getId())).thenReturn(testPost);
        when(mediaService.addMedia(any(Media.class))).thenReturn(false);
        assertFalse(mediaController.addMedia(testPost.getId(), file));
    }

    @Test
    public void testAddMediaEmptyPostId() {
        when(postService.postExists(testPost.getId())).thenReturn(true);
        assertFalse(mediaController.addMedia("", file));
    }

    @Test
    public void testAddMediaEmptyFile() {
        assertFalse(mediaController.addMedia(testPost.getId(), null));
    }

    @Test
    public void testAddMediaEmptyPostIdAndFile() {
        assertFalse(mediaController.addMedia("", null));
    }

    @Test
    public void testDeleteMedia() {
        when(mediaService.deleteMedia(testMedia.getId())).thenReturn(true);
        when(mediaService.mediaExists(testMedia.getId())).thenReturn(true);
        assertTrue(mediaController.deleteMedia(testMedia.getId()));
    }

    @Test
    public void testDeleteMediaNotExists() {
        when(mediaService.mediaExists(testMedia.getId())).thenReturn(false);
        assertFalse(mediaController.deleteMedia(testMedia.getId()));
    }

    @Test
    public void testDeleteMediaEmptyMediaId() {
        assertFalse(mediaController.deleteMedia(""));
    }

    @Test
    public void testGetMedia() {
        when(mediaService.getMedia(testMedia.getId())).thenReturn(testMedia);
        when(mediaService.mediaExists(testMedia.getId())).thenReturn(true);
        assertEquals(testMedia, mediaController.getMedia(testMedia.getId()));
    }

    @Test
    public void testGetMediaNotExists() {
        when(mediaService.mediaExists(testMedia.getId())).thenReturn(false);
        assertNull(mediaController.getMedia(testMedia.getId()));
    }

    @Test
    public void testGetMediaEmptyMediaId() {
        assertNull(mediaController.getMedia(""));
    }
}
