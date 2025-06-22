package example.flashchat.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table (name = "media")
public class Media {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn (name = "post_id")
    @NotNull
    private Post post;

    @NotBlank
    private String filePath;


    public Media() {
        this.id = UUID.randomUUID();
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setPost(Post post) {
        this.post = post;
    }

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
