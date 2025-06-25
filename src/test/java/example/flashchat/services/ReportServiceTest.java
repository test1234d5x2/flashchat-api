package example.flashchat.services;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import example.flashchat.models.Post;
import example.flashchat.models.Report;
import example.flashchat.models.User;
import example.flashchat.repositories.ReportRepo;

public class ReportServiceTest {
    @Mock
    private ReportRepo reportRepo;

    @InjectMocks
    private ReportService reportService;

    private Report testReport;
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

        testReport = new Report();
        testReport.setReporter(testUser);
        testReport.setPost(testPost);
        testReport.setReason("test reason");
    }


    @Test
    public void testAddReport() {
        when(reportRepo.save(testReport)).thenReturn(testReport);
        assertTrue(reportService.addReport(testReport));
    }

    @Test
    public void testDeleteReport() {
        when(reportRepo.existsById(testReport.getId())).thenReturn(true);
        assertTrue(reportService.deleteReport(testReport.getId()));
    }

    @Test
    public void testReportExists() {
        when(reportRepo.existsById(testReport.getId())).thenReturn(true);
        assertTrue(reportService.reportExists(testReport.getId()));
    }
}
