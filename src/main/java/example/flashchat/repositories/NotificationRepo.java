package example.flashchat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import example.flashchat.models.Notification;
import example.flashchat.models.User;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, String> {
    List<Notification> findByRecepientUser(User user);
}
