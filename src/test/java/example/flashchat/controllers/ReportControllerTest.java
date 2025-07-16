package example.flashchat.controllers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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

        testReport = new Report();
        testReport.setReporter(testUser);
        testReport.setPost(testPost);
        testReport.setReason("test reason");
    }

    private Authentication createMockAuthentication(String username, boolean authenticated) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        authentication.setAuthenticated(authenticated);
        return authentication;
    }


    private ReportRequest createReportRequest(String postId, String reason) {
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.postId = postId;
        reportRequest.reason = reason;
        return reportRequest;
    }

    @Test
    public void testReportPost() {
        when(userService.userExistsByUsername(testUser.getUsername())).thenReturn(true);
        when(postService.postExists(testPost.getId())).thenReturn(true);
        when(postService.retrievePostById(testPost.getId())).thenReturn(testPost);
        when(userService.findByUsername(testUser.getUsername())).thenReturn(testUser);
        when(reportService.addReport(any(Report.class))).thenReturn(true);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        boolean result = reportController.addReport(authentication, createReportRequest(testPost.getId(), "test reason"));
        assertTrue(result);
    }

    @Test
    public void testReportPostNotExists() {
        when(userService.userExistsByUsername(testUser.getUsername())).thenReturn(true);
        when(postService.postExists(testPost.getId())).thenReturn(false);
        when(postService.retrievePostById(testPost.getPost())).thenReturn(testPost);
        when(userService.findByUsername(testUser.getUsername())).thenReturn(testUser);
        when(reportService.addReport(any(Report.class))).thenReturn(true);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        boolean result = reportController.addReport(authentication, createReportRequest(testPost.getId(), "test reason"));
        assertFalse(result);
    }

    @Test
    public void testReportEmptyUserAuthentication() {
        when(userService.userExistsByUsername(testUser.getUsername())).thenReturn(true);
        when(postService.postExists(testPost.getId())).thenReturn(false);
        when(postService.retrievePostById(testPost.getPost())).thenReturn(testPost);
        when(userService.findByUsername(testUser.getUsername())).thenReturn(testUser);
        when(reportService.addReport(any(Report.class))).thenReturn(true);

        boolean result = reportController.addReport(null, createReportRequest(testPost.getPost(), "test reason"));
        assertFalse(result);
    }

    @Test
    public void testReportEmptyPostId() {
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);
        boolean result = reportController.addReport(authentication, createReportRequest("", "test reason"));
        assertFalse(result);
    }

    @Test
    public void testReportEmptyReason() {
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);
        boolean result = reportController.addReport(authentication, createReportRequest(testPost.getPost(), ""));
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
