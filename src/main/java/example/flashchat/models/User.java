package example.flashchat.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "users")
public class User extends LoginDetails {

    @Id
    private String id;

    @NotBlank
    @Column(unique = true)
    private String handle;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "likedBy", orphanRemoval = true)
    private List<Like> likedPosts = new ArrayList<>();

    @OneToMany(mappedBy = "follower", orphanRemoval = true)
    private List<Follow> following = new ArrayList<>();

    @OneToMany(mappedBy = "followed", orphanRemoval = true)
    private List<Follow> followers = new ArrayList<>();

    @OneToMany(mappedBy = "reporter",orphanRemoval = true)
    private List<Report> reports = new ArrayList<>();

    @OneToMany(mappedBy = "user1", orphanRemoval = true)
    private List<Chat> chatsAsUser1 = new ArrayList<>();

    @OneToMany(mappedBy = "user2", orphanRemoval = true)
    private List<Chat> chatsAsUser2 = new ArrayList<>();

    @OneToMany(mappedBy = "sender", orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "recepientUser", orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "actionUser", orphanRemoval = true)
    private List<Notification> notificationsAsActionUser = new ArrayList<>();

    public User() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getHandle() {
        return handle;
    }

    public int getPostsCount() {
        return posts.size();
    }

    @JsonIgnore
    public List<Like> getLikedPosts() {
        return likedPosts;
    }

    @JsonIgnore
    public List<Follow> getFollowers() {
        return followers;
    }

    public int getFollowersCount() {
        return followers.size();
    }

    @JsonIgnore
    public List<Follow> getFollowing() {
        return following;
    }

    public int getFollowingCount() {
        return following.size();
    }

    @JsonIgnore
    public List<Chat> getChatsAsUser1() {
        return chatsAsUser1;
    }

    @JsonIgnore
    public List<Chat> getChatsAsUser2() {
        return chatsAsUser2;
    }

    @JsonIgnore
    public List<Message> getMessages() {
        return messages;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    @JsonIgnore
    public List<Notification> getNotificationsAsActionUser() {
        return notificationsAsActionUser;
    }
}
