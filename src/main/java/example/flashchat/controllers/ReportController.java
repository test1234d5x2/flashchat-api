package example.flashchat.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import example.flashchat.services.PostService;
import example.flashchat.services.UserService;
import example.flashchat.services.ReportService;
import example.flashchat.models.User;
import example.flashchat.models.Post;
import example.flashchat.models.Report;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @PostMapping
    public boolean addReport(@RequestBody ReportRequest reportRequest) {
        String reporterId = reportRequest.reporterId;
        String postId = reportRequest.postId;
        String reason = reportRequest.reason;

        if (reporterId.isEmpty() || postId.isEmpty() || reason.isEmpty()) {
            // Empty check.
            return false;
        }

        if (!userService.userExists(reporterId) || !postService.postExists(postId)) {
            // User and post existence check.
            return false;
        }

        User reporter = userService.findById(reporterId);
        Post post = postService.retrievePostById(postId);

        Report report = new Report();
        report.setReporter(reporter);
        report.setPost(post);
        report.setReason(reason);

        return reportService.addReport(report);
    }

    @DeleteMapping
    public boolean deleteReport(@RequestParam String reportId) {
        if (reportId.isEmpty()) {
            // Empty check.
            return false;
        }

        if (!reportService.reportExists(reportId)) {
            // Report existence check.
            return false;
        }

        return reportService.deleteReport(reportId);
    }
}

class ReportRequest {
    public String reporterId;
    public String postId;
    public String reason;
}