package example.flashchat.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import example.flashchat.models.Post;
import example.flashchat.repositories.PostRepo;
import example.flashchat.models.User;

public class PostServiceTest {
    @Mock
    private PostRepo postRepo;

    @InjectMocks
    private PostService postService;

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
    }

    @Test
    public void testPostExists() {
        when(postRepo.findById(testPost.getId())).thenReturn(Optional.of(testPost));
        assertTrue(postService.postExists(testPost.getId()));
    }

    @Test
    public void testPostDoesNotExist() {
        when(postRepo.findById(testPost.getId())).thenReturn(Optional.empty());
        assertFalse(postService.postExists(testPost.getId()));
    }

    @Test
    public void testRetrievePostById() {
        when(postRepo.findById(testPost.getId())).thenReturn(Optional.of(testPost));
        Post result = postService.retrievePostById(testPost.getId());
        assertEquals(testPost, result);
    }

    @Test
    public void testRetrievePostByIdDoesNotExist() {
        when(postRepo.findById(testPost.getId())).thenReturn(Optional.empty());
        Post result = postService.retrievePostById(testPost.getId());
        assertNull(result);
    }

    @Test
    public void testCreatePost() {
        when(postRepo.save(testPost)).thenReturn(testPost);
        assertTrue(postService.createPost(testPost));
    }

    @Test
    public void testGetPosts() {
        when(postRepo.findByUserId(testUser.getId())).thenReturn(List.of(testPost));
        List<Post> result = postService.getPosts(testUser.getId());
        assertEquals(List.of(testPost), result);
    }

    @Test
    public void testDeletePost() {
        when(postRepo.findById(testPost.getId())).thenReturn(Optional.of(testPost));
        assertTrue(postService.deletePost(testPost.getId()));
    }
}
