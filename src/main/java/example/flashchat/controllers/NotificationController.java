package example.flashchat.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import example.flashchat.enums.NotificationType;
import example.flashchat.models.Notification;
import example.flashchat.models.User;
import example.flashchat.services.NotificationService;
import example.flashchat.services.UserService;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @PostMapping
    public boolean createNotification(Authentication authentication, @RequestParam String actionUserId, @RequestParam String message, @RequestParam NotificationType type) {
        if (authentication == null) {
            System.out.println("No authentication present");
            return false;
        }

        String username = authentication.getName().toString(); // recipientUser

        if (actionUserId.isEmpty() || message.isEmpty() || type == null) {
            return false;
        }

        if (!userService.userExists(actionUserId) || !userService.userExistsByUsername(username)) {
            return false;
        }

        User receipientUser = userService.findByUsername(username);
        User actionUser = userService.findById(actionUserId);

        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setType(type);
        notification.setRecepientUser(receipientUser);
        notification.setActionUser(actionUser);

        return notificationService.createNotification(notification);
    }

    @GetMapping
    public List<Notification> getNotifications(Authentication authentication) {
        if (authentication == null) {
            System.out.println("No authentication present");
            return new ArrayList<>();
        }

        String username = authentication.getName().toString();

        if (!userService.userExistsByUsername(username)) {
            return null;
        }

        User user = userService.findByUsername(username);
        return notificationService.getNotifications(user);
    }

    @PostMapping("/mark-as-read")
    public boolean markAsRead(@RequestBody NotificationMarkAsReadRequest request) {
        String notificationId = request.notificationId;

        if (notificationId.isEmpty()) {
            return false;
        }

        if (!notificationService.notificationExists(notificationId)) {
            return false;
        }

        Notification notification = notificationService.findById(notificationId);
        return notificationService.markAsRead(notification);
    }

    @PostMapping("/mark-as-unread")
    public boolean markAsUnread(@RequestParam String notificationId) {
        if (notificationId.isEmpty()) {
            return false;
        }

        if (!notificationService.notificationExists(notificationId)) {
            return false;
        }

        Notification notification = notificationService.findById(notificationId);
        return notificationService.markAsUnread(notification);
    }

    @PostMapping("/delete")
    public boolean deleteNotification(@RequestParam String notificationId) {
        if (notificationId.isEmpty()) {
            return false;
        }

        if (!notificationService.notificationExists(notificationId)) {
            return false;
        }

        Notification notification = notificationService.findById(notificationId);
        return notificationService.deleteNotification(notification);
    }

    @PostMapping("/delete-all")
    public boolean deleteAllNotifications(Authentication authentication) {
        if (authentication == null) {
            System.out.println("No authentication present");
            return false;
        }

        String username = authentication.getName().toString();

        if (!userService.userExistsByUsername(username)) {
            return false;
        }

        User user = userService.findByUsername(username);
        return notificationService.deleteAllNotifications(user);
    }
}


class NotificationMarkAsReadRequest {
    public String notificationId;
}