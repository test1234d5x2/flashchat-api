package example.flashchat.controllers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import example.flashchat.services.CommentService;
import example.flashchat.services.PostService;
import example.flashchat.services.UserService;
import example.flashchat.models.Comment;
import example.flashchat.models.Post;
import example.flashchat.models.User;
import example.flashchat.services.NotificationService;
import example.flashchat.models.Notification;

public class CommentControllerTest {
    
    @InjectMocks
    private CommentController commentController;

    @Mock
    private CommentService commentService;

    @Mock
    private PostService postService;

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    private Comment testComment;
    private Post testPost;
    private User testUser;
    private Comment testParentComment;

    private final String AUTHENTICATED_USER_USERNAME = "testuser";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setUsername(AUTHENTICATED_USER_USERNAME);
        testUser.setPassword("password");

        testPost = new Post();
        testPost.setUser(testUser);
        testPost.setPost("test post");

        testComment = new Comment();
        testComment.setPost(testPost);
        testComment.setUser(testUser);
        testComment.setComment("test comment");

        testParentComment = new Comment();
        testParentComment.setPost(testPost);
        testParentComment.setUser(testUser);
        testParentComment.setComment("test parent comment");
    }

    private Authentication createMockAuthentication(String username, boolean authenticated) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        authentication.setAuthenticated(authenticated);
        return authentication;
    }


    @Test
    public void testCreateCommentWithoutParent() {
        when(commentService.createComment(any(Comment.class))).thenReturn(true);
        when(postService.postExists(testPost.getId())).thenReturn(true);
        when(userService.userExists(testUser.getId())).thenReturn(true);
        when(userService.userExistsByUsername(testUser.getUsername())).thenReturn(true);
        when(postService.retrievePostById(testPost.getId())).thenReturn(testPost);
        when(userService.findById(testUser.getId())).thenReturn(testUser);
        when(userService.findByUsername(testUser.getUsername())).thenReturn(testUser);
        when(notificationService.createNotification(any(Notification.class))).thenReturn(true);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        CommentRequest request = new CommentRequest();
        request.postId = testPost.getId();
        request.comment = testComment.getComment();

        assertTrue(commentController.createComment(authentication, request));
    }

    @Test
    public void testCreateCommentWithoutParentInvalidPostId() {
        when(postService.postExists(testPost.getId())).thenReturn(false);
        when(userService.userExistsByUsername(testUser.getUsername())).thenReturn(true);
        
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        CommentRequest request = new CommentRequest();
        request.postId = testPost.getId();
        request.comment = testComment.getComment();

        assertFalse(commentController.createComment(authentication, request));
    }

    @Test
    public void testCreateCommentWithoutParentInvalidUserId() {
        when(postService.postExists(testPost.getId())).thenReturn(true);
        when(userService.userExistsByUsername(testUser.getUsername())).thenReturn(false);
        
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        CommentRequest request = new CommentRequest();
        request.postId = testPost.getId();
        request.comment = testComment.getComment();

        assertFalse(commentController.createComment(authentication, request));
    }

    @Test
    public void testCreateCommentEmptyPostId() {
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        CommentRequest request = new CommentRequest();
        request.postId = null;
        request.comment = testComment.getComment();

        assertFalse(commentController.createComment(authentication, request));
    }

    @Test
    public void testCreateCommentEmptyComment() {
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        CommentRequest request = new CommentRequest();
        request.postId = testPost.getId();
        request.comment = null;

        assertFalse(commentController.createComment(authentication, request));
    }

    @Test
    public void testCreateCommentWithParent() {
        when(commentService.createComment(any(Comment.class))).thenReturn(true);
        when(postService.postExists(testPost.getId())).thenReturn(true);
        when(userService.userExists(testUser.getId())).thenReturn(true);
        when(userService.userExistsByUsername(testUser.getUsername())).thenReturn(true);
        when(postService.retrievePostById(testPost.getId())).thenReturn(testPost);
        when(userService.findById(testUser.getId())).thenReturn(testUser);
        when(userService.findByUsername(testUser.getUsername())).thenReturn(testUser);
        when(commentService.commentExists(testParentComment.getId())).thenReturn(true);
        when(commentService.getComment(testParentComment.getId())).thenReturn(testParentComment);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        CommentRequest request = new CommentRequest();
        request.postId = testPost.getId();
        request.comment = testComment.getComment();
        request.parentCommentId = Optional.of(testParentComment.getId());
        
        assertTrue(commentController.createComment(authentication, request));
    }

    @Test
    public void testCreateCommentWithParentInvalidPostId() {
        when(postService.postExists(testPost.getId())).thenReturn(false);
        when(userService.userExists(testUser.getId())).thenReturn(true);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        CommentRequest request = new CommentRequest();
        request.postId = testPost.getId();
        request.comment = testComment.getComment();
        request.parentCommentId = Optional.of(testParentComment.getId());

        assertFalse(commentController.createComment(authentication, request));
    }

    @Test
    public void testCreateCommentWithParentInvalidParentCommentId() {
        when(commentService.commentExists(testParentComment.getId())).thenReturn(false);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        CommentRequest request = new CommentRequest();
        request.postId = testPost.getId();
        request.comment = testComment.getComment();
        request.parentCommentId = Optional.of("invalid");

        assertFalse(commentController.createComment(authentication, request));
    }
}
