package example.flashchat.controllers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import example.flashchat.services.CommentService;
import example.flashchat.services.PostService;
import example.flashchat.services.UserService;
import example.flashchat.models.Comment;
import example.flashchat.models.Post;
import example.flashchat.models.User;

public class CommentControllerTest {
    
    @InjectMocks
    private CommentController commentController;

    @Mock
    private CommentService commentService;

    @Mock
    private PostService postService;

    @Mock
    private UserService userService;

    private CommentRequest testCommentRequest;
    private Comment testComment;
    private Post testPost;
    private User testUser;
    private Comment testParentComment;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");

        testPost = new Post();
        testPost.setUser(testUser);
        testPost.setPost("test post");

        testComment = new Comment();
        testComment.setPost(testPost);
        testComment.setUser(testUser);
        testComment.setComment("test comment");

        testCommentRequest = new CommentRequest();
        testCommentRequest.postId = testPost.getId();
        testCommentRequest.userId = testUser.getId();
        testCommentRequest.comment = "test comment";
        testCommentRequest.parentCommentId = Optional.empty();
        

        testParentComment = new Comment();
        testParentComment.setPost(testPost);
        testParentComment.setUser(testUser);
        testParentComment.setComment("test parent comment");
    }

    @Test
    public void testCreateCommentWithoutParent() {
        when(commentService.createComment(any(Comment.class))).thenReturn(true);
        when(postService.postExists(testPost.getId())).thenReturn(true);
        when(userService.userExists(testUser.getId())).thenReturn(true);
        when(postService.retrievePostById(testPost.getId())).thenReturn(testPost);
        when(userService.findById(testUser.getId())).thenReturn(testUser);
        assertTrue(commentController.createComment(testCommentRequest));
    }

    @Test
    public void testCreateCommentWithoutParentInvalidPostId() {
        when(postService.postExists(testPost.getId())).thenReturn(false);
        when(userService.userExists(testUser.getId())).thenReturn(true);
        assertFalse(commentController.createComment(testCommentRequest));
    }

    @Test
    public void testCreateCommentWithoutParentInvalidUserId() {
        when(postService.postExists(testPost.getId())).thenReturn(true);
        when(userService.userExists(testUser.getId())).thenReturn(false);
        assertFalse(commentController.createComment(testCommentRequest));
    }

    @Test
    public void testCreateCommentEmptyPostId() {
        testCommentRequest.postId = null;
        assertFalse(commentController.createComment(testCommentRequest));
    }

    @Test
    public void testCreateCommentEmptyUserId() {
        testCommentRequest.userId = null;
        assertFalse(commentController.createComment(testCommentRequest));
    }

    @Test
    public void testCreateCommentEmptyComment() {
        testCommentRequest.comment = null;
        assertFalse(commentController.createComment(testCommentRequest));
    }

    @Test
    public void testCreateCommentWithParent() {
        testCommentRequest.parentCommentId = Optional.of(testParentComment.getId());
        when(commentService.createComment(any(Comment.class))).thenReturn(true);
        when(postService.postExists(testPost.getId())).thenReturn(true);
        when(userService.userExists(testUser.getId())).thenReturn(true);
        when(postService.retrievePostById(testPost.getId())).thenReturn(testPost);
        when(userService.findById(testUser.getId())).thenReturn(testUser);
        when(commentService.commentExists(testParentComment.getId())).thenReturn(true);
        when(commentService.getComment(testParentComment.getId())).thenReturn(testParentComment);
        assertTrue(commentController.createComment(testCommentRequest));
    }

    @Test
    public void testCreateCommentWithParentInvalidPostId() {
        testCommentRequest.parentCommentId = Optional.of(testParentComment.getId());
        when(postService.postExists(testPost.getId())).thenReturn(false);
        when(userService.userExists(testUser.getId())).thenReturn(true);
        assertFalse(commentController.createComment(testCommentRequest));
    }

    @Test
    public void testCreateCommentWithParentInvalidUserId() {
        testCommentRequest.parentCommentId = Optional.of(testParentComment.getId());
        when(postService.postExists(testPost.getId())).thenReturn(true);
        when(userService.userExists(testUser.getId())).thenReturn(false);
        assertFalse(commentController.createComment(testCommentRequest));
    }

    @Test
    public void testCreateCommentWithParentInvalidParentCommentId() {
        testCommentRequest.parentCommentId = Optional.of("invalid");
        when(commentService.commentExists(testParentComment.getId())).thenReturn(false);
        assertFalse(commentController.createComment(testCommentRequest));
    }
}
