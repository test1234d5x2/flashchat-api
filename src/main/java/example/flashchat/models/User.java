package example.flashchat.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "users")
public class User extends LoginDetails {

    @Id
    private String id;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "likedBy", orphanRemoval = true)
    private List<Like> likedPosts = new ArrayList<>();

    @OneToMany(mappedBy = "follower", orphanRemoval = true)
    private List<Follow> followers = new ArrayList<>();

    @OneToMany(mappedBy = "followed", orphanRemoval = true)
    private List<Follow> followedBy = new ArrayList<>();

    @OneToMany(mappedBy = "reporter",orphanRemoval = true)
    private List<Report> reports = new ArrayList<>();

    @OneToMany(mappedBy = "user1", orphanRemoval = true)
    private List<Chat> chatsAsUser1 = new ArrayList<>();

    @OneToMany(mappedBy = "user2", orphanRemoval = true)
    private List<Chat> chatsAsUser2 = new ArrayList<>();

    public User() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public List<Like> getLikedPosts() {
        return likedPosts;
    }

    public List<Follow> getFollowers() {
        return followers;
    }

    public List<Follow> getFollowedBy() {
        return followedBy;
    }

    public List<Chat> getChatsAsUser1() {
        return chatsAsUser1;
    }

    public List<Chat> getChatsAsUser2() {
        return chatsAsUser2;
    }
}
