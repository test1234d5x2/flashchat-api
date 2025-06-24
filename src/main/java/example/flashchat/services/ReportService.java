package example.flashchat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import example.flashchat.models.Post;
import example.flashchat.models.Report;
import example.flashchat.models.User;
import example.flashchat.repositories.ReportRepo;

@Service
public class ReportService {

    @Autowired
    private ReportRepo reportRepo;

    public boolean addReport(User reporter, Post post, String reason) {
        Report report = new Report();
        report.setReporter(reporter);
        report.setPost(post);
        report.setReason(reason);
        reportRepo.save(report);
        return true;
    }

    public boolean deleteReport(String reportId) {
        if (!reportRepo.existsById(reportId)) {
            return false;
        }
        
        reportRepo.deleteById(reportId);
        return true;
    }

    public boolean reportExists(String reportId) {
        return reportRepo.existsById(reportId);
    }
}
