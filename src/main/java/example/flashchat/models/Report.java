package example.flashchat.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.UUID;


@Entity
@Table (name = "reports")
public class Report {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @NotNull
    private Post reportedPost;

    @NotBlank
    private String reason;

    @NotNull
    private Date dateReported;

    public Report() {
        this.id = UUID.randomUUID().toString();
        this.dateReported = new Date();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public User getReporter() {
        return reporter;
    }

    public void setPost(Post post) {
        this.reportedPost = post;
    }

    public Post getPost() {
        return reportedPost;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public Date getDateReported() {
        return dateReported;
    }


}
