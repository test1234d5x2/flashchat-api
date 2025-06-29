package example.flashchat.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import example.flashchat.models.Notification;
import example.flashchat.models.User;
import example.flashchat.repositories.NotificationRepo;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepo notificationRepo;

    public boolean createNotification(Notification notification) {
        notificationRepo.save(notification);
        return true;
    }

    public List<Notification> getNotifications(User user) {
        return notificationRepo.findByRecepientUser(user);
    }

    public boolean notificationExists(String id) {
        return notificationRepo.existsById(id);
    }

    public Notification findById(String id) {
        return notificationRepo.findById(id).orElse(null);
    }

    public boolean markAsRead(Notification notification) {
        notification.setRead(true);
        notificationRepo.save(notification);
        return true;
    }

    public boolean markAsUnread(Notification notification) {
        notification.setRead(false);
        notificationRepo.save(notification);
        return true;
    }

    public boolean deleteNotification(Notification notification) {
        notificationRepo.delete(notification);
        return true;
    }

    public boolean deleteAllNotifications(User user) {
        List<Notification> notifications = notificationRepo.findByRecepientUser(user);
        notificationRepo.deleteAll(notifications);
        return true;
    }
}
