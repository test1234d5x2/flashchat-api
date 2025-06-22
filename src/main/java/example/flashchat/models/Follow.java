package example.flashchat.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table (name = "follows")
public class Follow {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn (name = "follower_user_id")
    @NotNull
    private User follower;

    @ManyToOne
    @JoinColumn (name = "followed_user_id")
    @NotNull
    private User followed;


    public Follow() {
        this.id = UUID.randomUUID();
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setFollower(User follower) {
        this.follower = follower;
    }

    public User getFollower() {
        return follower;
    }

    public void setFollowed(User followed) {
        this.followed = followed;
    }

    public User getFollowed() {
        return followed;
    }
}
