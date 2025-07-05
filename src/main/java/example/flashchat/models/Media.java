package example.flashchat.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table (name = "media")
public class Media {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn (name = "postId")
    @NotNull
    private Post post;

    @NotBlank
    private String filePath;


    public Media() {
        this.id = UUID.randomUUID().toString();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @JsonIgnore
    public Post getPost() {
        return post;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
