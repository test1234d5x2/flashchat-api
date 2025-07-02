package example.flashchat.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    private String id;

    @NotBlank
    private String comment;

    @NotNull
    private LocalDateTime datePosted;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    @Nullable
    private Comment parent;

    @OneToMany(mappedBy = "parent")
    private List<Comment> replies;

    public Comment() {
        this.id = UUID.randomUUID().toString();
        this.datePosted = LocalDateTime.now();
    }


    public String getId() {
        return id;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(LocalDateTime datePosted) {
        this.datePosted = datePosted;
    }

    @JsonIgnore
    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    public void setParent(Comment parent) {
        this.parent = parent;
    }

    @JsonIgnore
    public Comment getParent() {
        return parent;
    }

    public List<Comment> getReplies() {
        return replies;
    }
}
