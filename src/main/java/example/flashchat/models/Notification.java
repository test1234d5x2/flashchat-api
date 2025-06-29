package example.flashchat.models;

import java.time.LocalDateTime;
import java.util.UUID;

import example.flashchat.enums.NotificationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    private String id;

    @NotBlank
    private String message;
    
    @NotBlank
    private String type;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private boolean read = false;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "recipientId")
    private User recepientUser;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "actionUserId")
    private User actionUser;

    public Notification() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean getRead() {
        return read;
    }

    public void setType(NotificationType type) {
        this.type = type.toString();
    }

    public String getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setActionUser(User actionUser) {
        this.actionUser = actionUser;
    }

    public User getActionUser() {
        return actionUser;
    }

    public void setRecepientUser(User recepientUser) {
        this.recepientUser = recepientUser;
    }

    public User getRecepientUser() {
        return recepientUser;
    }

}