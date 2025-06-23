package example.flashchat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import example.flashchat.models.Report;
import java.util.List;
import example.flashchat.models.User;

public interface ReportRepo extends JpaRepository<Report, String> {
    List<Report> findByReporter(User reporter);
}
