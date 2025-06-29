package example.flashchat.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import example.flashchat.enums.NotificationType;
import example.flashchat.models.Notification;
import example.flashchat.models.User;
import example.flashchat.repositories.NotificationRepo;

@SpringBootTest
public class NotificationServiceTest {

    @Mock
    private NotificationRepo notificationRepo;

    @InjectMocks
    private NotificationService notificationService;

    private User receipientUser;
    private User actionUser;
    private Notification notification;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        receipientUser = new User();
        receipientUser.setUsername("testUser");
        receipientUser.setPassword("testPassword1");

        actionUser = new User();
        actionUser.setUsername("testActionUser");
        actionUser.setPassword("testPassword2");

        notification = new Notification();
        notification.setMessage("testMessage");
        notification.setType(NotificationType.FOLLOW);
        notification.setRecepientUser(receipientUser);
        notification.setActionUser(actionUser);
    }

    @Test
    public void testCreateNotification() {
        when(notificationRepo.save(notification)).thenReturn(notification);
        boolean result = notificationService.createNotification(notification);
        assertTrue(result);
    }

    @Test
    public void testGetNotifications() {
        when(notificationRepo.findByRecepientUser(receipientUser)).thenReturn(List.of(notification));
        List<Notification> result = notificationService.getNotifications(receipientUser);
        assertEquals(1, result.size());
        assertEquals(notification, result.get(0));
    }

    @Test
    public void testNotificationExists() {
        when(notificationRepo.existsById(notification.getId())).thenReturn(true);
        boolean result = notificationService.notificationExists(notification.getId());
        assertTrue(result);
    }

    @Test
    public void testNotificationDoesNotExist() {
        when(notificationRepo.existsById(notification.getId())).thenReturn(false);
        boolean result = notificationService.notificationExists(notification.getId());
        assertFalse(result);
    }

    @Test
    public void testFindById() {
        when(notificationRepo.findById(notification.getId())).thenReturn(Optional.of(notification));
        Notification result = notificationService.findById(notification.getId());
        assertEquals(notification, result);
    }

    @Test
    public void testFindByIdDoesNotExist() {
        when(notificationRepo.findById(notification.getId())).thenReturn(Optional.empty());
        Notification result = notificationService.findById(notification.getId());
        assertNull(result);
    }

    @Test
    public void testMarkAsRead() {
        when(notificationRepo.save(notification)).thenReturn(notification);
        boolean result = notificationService.markAsRead(notification);
        assertTrue(result);
    }

    @Test
    public void testMarkAsUnread() {
        when(notificationRepo.save(notification)).thenReturn(notification);
        boolean result = notificationService.markAsUnread(notification);
        assertTrue(result);
    }

    @Test
    public void testDeleteNotification() {
        doNothing().when(notificationRepo).delete(notification);
        boolean result = notificationService.deleteNotification(notification);
        assertTrue(result);
    }

    @Test
    public void testDeleteAllNotifications() {
        when(notificationRepo.findByRecepientUser(receipientUser)).thenReturn(List.of(notification));
        doNothing().when(notificationRepo).deleteAll(List.of(notification));
        boolean result = notificationService.deleteAllNotifications(receipientUser);
        assertTrue(result);
    }
}
