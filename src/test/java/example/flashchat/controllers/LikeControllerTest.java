package example.flashchat.controllers;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import example.flashchat.models.Like;
import example.flashchat.models.Post;
import example.flashchat.models.User;
import example.flashchat.services.LikeService;
import example.flashchat.services.PostService;
import example.flashchat.services.UserService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;

public class LikeControllerTest {
    @Mock
    private LikeService likeService;

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    @InjectMocks
    private LikeController likeController;

    private User testUser;
    private User testUser2;
    private Post testPost;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");

        testUser2 = new User();
        testUser2.setUsername("testuser2");
        testUser2.setPassword("password2");

        testPost = new Post();
        testPost.setUser(testUser);
        testPost.setPost("test post");
    }

    private LikeRequest createLikeRequest(String postId, String userId) {
        LikeRequest request = new LikeRequest();
        request.postId = postId;
        request.userId = userId;
        return request;
    }

    @Test
    public void testAddLike() {
        when(postService.postExists(testPost.getId())).thenReturn(true);
        when(userService.userExists(testUser2.getId())).thenReturn(true);
        when(userService.findById(testUser2.getId())).thenReturn(testUser2);
        when(likeService.likeExists(testPost, testUser2)).thenReturn(false);
        when(postService.retrievePostById(testPost.getId())).thenReturn(testPost);
        when(likeService.addLike(any(Like.class))).thenReturn(true);
        assertTrue(likeController.addLike(createLikeRequest(testPost.getId(), testUser2.getId())));
    }

    @Test
    public void testLikeAlreadExists() {
        when(likeService.likeExists(testPost, testUser2)).thenReturn(true);
        assertFalse(likeController.addLike(createLikeRequest(testPost.getId(), testUser2.getId())));
    }

    @Test
    public void testUserLikesOwnPost() {
        when(postService.postExists(testPost.getId())).thenReturn(true);
        when(userService.userExists(testUser.getId())).thenReturn(true);
        when(postService.retrievePostById(testPost.getId())).thenReturn(testPost);
        when(userService.findById(testUser.getId())).thenReturn(testUser);
        when(likeService.likeExists(testPost, testUser)).thenReturn(false);
        assertFalse(likeController.addLike(createLikeRequest(testPost.getId(), testUser.getId())));
    }

    @Test
    public void testAddLikeInvalidPostId() {
        when(postService.postExists(testPost.getId())).thenReturn(false);
        assertFalse(likeController.addLike(createLikeRequest(testPost.getId(), testUser2.getId())));
    }

    @Test
    public void testAddLikeInvalidUserId() {
        when(postService.postExists(testPost.getId())).thenReturn(true);
        when(userService.userExists(testUser2.getId())).thenReturn(false);
        assertFalse(likeController.addLike(createLikeRequest(testPost.getId(), testUser2.getId())));
    }

    @Test
    public void testEmptyPostId() {
        assertFalse(likeController.addLike(createLikeRequest("", testUser2.getId())));
    }

    @Test
    public void testEmptyUserId() {
        assertFalse(likeController.addLike(createLikeRequest(testPost.getId(), "")));
    }

    @Test
    public void testDeleteLike() {
        when(postService.postExists(testPost.getId())).thenReturn(true);
        when(userService.userExists(testUser2.getId())).thenReturn(true);
        when(userService.findById(testUser2.getId())).thenReturn(testUser2);
        when(postService.retrievePostById(testPost.getId())).thenReturn(testPost);
        when(likeService.likeExists(testPost, testUser2)).thenReturn(true);
        when(likeService.deleteLike(testPost, testUser2)).thenReturn(true);
        assertTrue(likeController.deleteLike(createLikeRequest(testPost.getId(), testUser2.getId())));
    }

    @Test
    public void testDeleteLikeInvalidLike() {
        when(likeService.likeExists(testPost, testUser2)).thenReturn(false);
        assertFalse(likeController.deleteLike(createLikeRequest(testPost.getId(), testUser2.getId())));
    }

    @Test
    public void testDeleteLikeInvalidPostId() {
        when(postService.postExists(testPost.getId())).thenReturn(false);
        assertFalse(likeController.deleteLike(createLikeRequest(testPost.getId(), testUser2.getId())));
    }

    @Test
    public void testDeleteLikeInvalidUserId() {
        when(postService.postExists(testPost.getId())).thenReturn(true);
        when(userService.userExists(testUser2.getId())).thenReturn(false);
        assertFalse(likeController.deleteLike(createLikeRequest(testPost.getId(), testUser2.getId())));
    }
    
    @Test
    public void testDeleteLikeEmptyPostId() {
        assertFalse(likeController.deleteLike(createLikeRequest("", testUser2.getId())));
    }

    @Test
    public void testDeleteLikeEmptyUserId() {
        assertFalse(likeController.deleteLike(createLikeRequest(testPost.getId(), "")));
    }

    @Test
    public void testGetLikedPosts() {
        when(userService.findById(testUser.getId())).thenReturn(testUser);
        when(userService.findById(testUser2.getId())).thenReturn(testUser2);
        assertTrue(likeController.getLikedPosts(testUser.getId()).isEmpty());
    }

    @Test
    public void testGetLikedPostsInvalidUserId() {
        when(userService.userExists(testUser.getId())).thenReturn(false);
        assertTrue(likeController.getLikedPosts(testUser.getId()).isEmpty());
    }

    @Test
    public void testGetLikedPostsEmptyUserId() {
        assertTrue(likeController.getLikedPosts("").isEmpty());
    }

    @Test
    public void testCheckLike_BothIdsEmpty() {
        LikeRequest request = createLikeRequest("", "");
        assertFalse(likeController.checkLike(request));
    }

    @Test
    public void testCheckLike_PostOrUserDoesNotExist() {
        LikeRequest request = createLikeRequest("postId", "userId");
        when(postService.postExists("postId")).thenReturn(false);
        when(userService.userExists("userId")).thenReturn(true);
        assertFalse(likeController.checkLike(request));

        when(postService.postExists("postId")).thenReturn(true);
        when(userService.userExists("userId")).thenReturn(false);
        assertFalse(likeController.checkLike(request));
    }

    @Test
    public void testCheckLike_LikeExists() {
        LikeRequest request = createLikeRequest("postId", "userId");
        when(postService.postExists("postId")).thenReturn(true);
        when(userService.userExists("userId")).thenReturn(true);
        Post post = new Post();
        User user = new User();
        when(postService.retrievePostById("postId")).thenReturn(post);
        when(userService.findById("userId")).thenReturn(user);
        when(likeService.likeExists(post, user)).thenReturn(true);
        assertTrue(likeController.checkLike(request));
    }

    @Test
    public void testCheckLike_LikeDoesNotExist() {
        LikeRequest request = createLikeRequest("postId", "userId");
        when(postService.postExists("postId")).thenReturn(true);
        when(userService.userExists("userId")).thenReturn(true);
        Post post = new Post();
        User user = new User();
        when(postService.retrievePostById("postId")).thenReturn(post);
        when(userService.findById("userId")).thenReturn(user);
        when(likeService.likeExists(post, user)).thenReturn(false);
        assertFalse(likeController.checkLike(request));
    }
}