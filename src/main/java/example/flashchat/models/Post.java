package example.flashchat.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table (name = "posts")
public class Post {

    @Id
    private String id;

    @NotBlank
    private String post;

    @NotNull
    private LocalDateTime datePosted;

    @NotNull
    private int views = 0;

    @ManyToOne
    @JoinColumn(name = "userId")
    @NotNull
    private User user;

    @OneToMany (mappedBy = "post", orphanRemoval = true)
    private List<Media> media = new ArrayList<>();

    @OneToMany (mappedBy = "postLiked", orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @OneToMany (mappedBy = "reportedPost", orphanRemoval = true)
    private List<Report> reports = new ArrayList<>();

    @OneToMany (mappedBy = "post", orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany (mappedBy = "reportedPost", orphanRemoval = true)
    private List<Report> postReports = new ArrayList<>();


    public Post() {
        this.id = UUID.randomUUID().toString();
        this.datePosted = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public LocalDateTime getDatePosted() {
        return datePosted;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void incrementViews() {
        this.views++;
    }

    public int getViews() {
        return views;
    }

    public List<Media> getMedia() {
        return media;
    }

    public void setMedia(List<Media> media) {
        this.media = media;
    }

    @JsonIgnore
    public List<Report> getReports() {
        return reports;
    }
}
