package example.flashchat.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table (name = "likes")
public class Like {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn (name = "post_id")
    @NotNull
    private Post postLiked;

    @ManyToOne
    @JoinColumn (name = "user_id")
    @NotNull
    private User likedBy;


    public Like() {
        this.id = UUID.randomUUID();
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
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
}
