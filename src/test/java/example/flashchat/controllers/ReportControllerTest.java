package example.flashchat.controllers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import example.flashchat.models.Post;
import example.flashchat.models.Report;
import example.flashchat.models.User;
import example.flashchat.services.PostService;
import example.flashchat.services.ReportService;
import example.flashchat.services.UserService;

public class ReportControllerTest {
    @Mock
    private ReportService reportService;

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    @InjectMocks
    private ReportController reportController;

    private User testUser;
    private Post testPost;
    private Report testReport;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");

        testPost = new Post();
        testPost.setUser(testUser);
        testPost.setPost("test post");

        testReport = new Report();
        testReport.setReporter(testUser);
        testReport.setPost(testPost);
        testReport.setReason("test reason");
    }

    private ReportRequest createReportRequest(String reporterId, String postId, String reason) {
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.reporterId = reporterId;
        reportRequest.postId = postId;
        reportRequest.reason = reason;
        return reportRequest;
    }

    @Test
    public void testReportPost() {
        when(userService.userExists(testUser.getId())).thenReturn(true);
        when(postService.postExists(testPost.getPost())).thenReturn(true);
        when(postService.retrievePostById(testPost.getPost())).thenReturn(testPost);
        when(userService.findById(testUser.getId())).thenReturn(testUser);
        when(reportService.addReport(any(Report.class))).thenReturn(true);
        boolean result = reportController.addReport(createReportRequest(testUser.getId(), testPost.getPost(), "test reason"));
        assertTrue(result);
    }

    @Test
    public void testReportPostNotExists() {
        when(userService.userExists(testUser.getId())).thenReturn(false);
        boolean result = reportController.addReport(createReportRequest(testUser.getId(), testPost.getPost(), "test reason"));
        assertFalse(result);
    }

    @Test
    public void testReportUserNotExists() {
        when(userService.userExists(testUser.getId())).thenReturn(true);
        when(postService.postExists(testPost.getPost())).thenReturn(false);
        boolean result = reportController.addReport(createReportRequest(testUser.getId(), testPost.getPost(), "test reason"));
        assertFalse(result);
    }

    @Test
    public void testReportEmptyUserId() {
        boolean result = reportController.addReport(createReportRequest("", testPost.getPost(), "test reason"));
        assertFalse(result);
    }

    @Test
    public void testReportEmptyPostId() {
        boolean result = reportController.addReport(createReportRequest(testUser.getId(), "", "test reason"));
        assertFalse(result);
    }

    @Test
    public void testReportEmptyReason() {
        boolean result = reportController.addReport(createReportRequest(testUser.getId(), testPost.getPost(), ""));
        assertFalse(result);
    }

    @Test
    public void testDeleteReport() {
        when(reportService.reportExists(testReport.getId())).thenReturn(true);
        when(reportService.deleteReport(testReport.getId())).thenReturn(true);
        boolean result = reportController.deleteReport(testReport.getId());
        assertTrue(result);
    }

    @Test
    public void testDeleteReportNotExists() {
        when(reportService.reportExists(testReport.getId())).thenReturn(false);
        boolean result = reportController.deleteReport(testReport.getId());
        assertFalse(result);
    }

    @Test
    public void testDeleteReportEmptyReportId() {
        boolean result = reportController.deleteReport("");
        assertFalse(result);
    }
}
