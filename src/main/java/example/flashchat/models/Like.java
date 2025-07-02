package example.flashchat.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table (name = "likes")
public class Like {

    @Id
    private String id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn (name = "postId")
    @NotNull
    private Post postLiked;

    @JsonIgnore
    @ManyToOne
    @JoinColumn (name = "userId")
    @NotNull
    private User likedBy;


    public Like() {
        this.id = UUID.randomUUID().toString();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setPostLiked(Post postLiked) {
        this.postLiked = postLiked;
    }

    public Post getPostLiked() {
        return postLiked;
    }

    public void setLikedBy(User likedBy) {
        this.likedBy = likedBy;
    }

    public User getLikedBy() {
        return likedBy;
    }

    public String getLikedById() {
        return likedBy.getId();
    }
}
