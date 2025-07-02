package example.flashchat.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import example.flashchat.models.Comment;
import example.flashchat.models.Post;
import example.flashchat.models.User;
import example.flashchat.repositories.CommentRepo;


// With parent comment.
@SpringBootTest
public class CommentServiceTest1 {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepo commentRepo;

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

        testParentComment = new Comment();
        testParentComment.setPost(testPost);
        testParentComment.setUser(testUser);
        testParentComment.setComment("test parent comment");
        
        testComment = new Comment();
        testComment.setPost(testPost);
        testComment.setUser(testUser);
        testComment.setComment("test comment");
        testComment.setParent(testParentComment);
    }

    @Test
    public void testCreateComment() {
        when(commentRepo.save(testComment)).thenReturn(testComment);
        assertTrue(commentService.createComment(testComment));
    }

    @Test
    public void testDeleteComment() {
        when(commentRepo.existsById(testComment.getId())).thenReturn(true);
        assertTrue(commentService.deleteComment(testComment.getId()));
    }

    @Test
    public void testCommentExists() {
        when(commentRepo.existsById(testComment.getId())).thenReturn(true);
        assertTrue(commentService.commentExists(testComment.getId()));
    }

    @Test
    public void testGetComment() {
        when(commentRepo.findById(testComment.getId())).thenReturn(Optional.of(testComment));
        assertEquals(testComment, commentService.getComment(testComment.getId()));
    }

    @Test
    public void testGetComments() {
        when(commentRepo.findByPostId(testPost.getId())).thenReturn(List.of(testComment));
        assertEquals(List.of(testComment), commentService.getComments(testPost.getId()));
    }
}
