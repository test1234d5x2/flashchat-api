package example.flashchat.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import example.flashchat.enums.NotificationType;
import example.flashchat.models.Notification;
import example.flashchat.models.User;
import example.flashchat.services.NotificationService;
import example.flashchat.services.UserService;

public class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationController notificationController;

    private User user1;
    private User user2;
    private Notification notification;

    private final String AUTHENTICATED_USER_USERNAME = "user1";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user1 = new User();
        user1.setUsername(AUTHENTICATED_USER_USERNAME);
        user1.setPassword("password");

        user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("password");

        notification = new Notification();
        notification.setMessage("Hello, user1!");
        notification.setType(NotificationType.MESSAGE);
        notification.setActionUser(user1);
        notification.setRecepientUser(user2);
    }

    private Authentication createMockAuthentication(String username, boolean authenticated) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        authentication.setAuthenticated(authenticated);
        return authentication;
    }


    @Test
    public void testCreateNotification() {
        when(userService.userExists(user1.getId())).thenReturn(true);
        when(userService.userExists(user2.getId())).thenReturn(true);
        when(userService.userExistsByUsername(user1.getUsername())).thenReturn(true);
        when(userService.userExistsByUsername(user2.getUsername())).thenReturn(true);
        when(userService.findById(user1.getId())).thenReturn(user1);
        when(userService.findById(user2.getId())).thenReturn(user2);
        when(userService.findByUsername(user1.getUsername())).thenReturn(user1);
        when(userService.findByUsername(user2.getUsername())).thenReturn(user2);
        when(notificationService.createNotification(any(Notification.class))).thenReturn(true);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        boolean result = notificationController.createNotification(authentication, user2.getId(), "Hello, user1!", NotificationType.MESSAGE);
        assertTrue(result);
    }

    @Test
    public void testCreateNotificationWithInvalidRecepient() {
        when(userService.userExists(user1.getId())).thenReturn(true);
        when(userService.userExists(user2.getId())).thenReturn(false);
        when(userService.userExistsByUsername(user1.getUsername())).thenReturn(true);
        when(userService.userExistsByUsername(user2.getUsername())).thenReturn(false);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        boolean result = notificationController.createNotification(authentication, user2.getId(), "Hello, user1!", NotificationType.MESSAGE);
        assertFalse(result);
    }

    @Test
    public void testCreateNotificationWithInvalidActionUser() {
        when(userService.userExists(user2.getId())).thenReturn(false);
        when(userService.userExistsByUsername(user2.getUsername())).thenReturn(false);
        when(userService.userExistsByUsername(user1.getUsername())).thenReturn(true);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        boolean result = notificationController.createNotification(authentication, user2.getId(), "Hello, user1!", NotificationType.MESSAGE);
        assertFalse(result);
    }

    @Test
    public void testCreateNotificationWithInvalidMessage() {
        when(userService.userExists(user1.getId())).thenReturn(true);
        when(userService.userExists(user2.getId())).thenReturn(true);
        when(userService.userExistsByUsername(user1.getUsername())).thenReturn(true);
        when(userService.userExistsByUsername(user2.getUsername())).thenReturn(true);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        boolean result = notificationController.createNotification(authentication, user2.getId(), "", NotificationType.MESSAGE);
        assertFalse(result);
    }

    @Test
    public void testCreateNotificationWithInvalidType() {
        when(userService.userExists(user1.getId())).thenReturn(true);
        when(userService.userExists(user2.getId())).thenReturn(true);
        when(userService.userExistsByUsername(user1.getUsername())).thenReturn(true);
        when(userService.userExistsByUsername(user2.getUsername())).thenReturn(true);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        boolean result = notificationController.createNotification(authentication, user2.getId(), "Hello, user1!", null);
        assertFalse(result);
    }
    
    @Test
    public void testCreateNotificationWithInvalidActionUserId() {
        when(userService.userExists(user1.getUsername())).thenReturn(true);
        when(userService.userExists(user2.getUsername())).thenReturn(true);
        when(userService.userExistsByUsername(user1.getUsername())).thenReturn(true);
        when(userService.userExistsByUsername(user2.getUsername())).thenReturn(true);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        boolean result = notificationController.createNotification(authentication, "", "Hello, user1!", NotificationType.MESSAGE);
        assertFalse(result);
    }

    @Test
    public void testGetNotifications() {
        when(userService.userExists(user1.getId())).thenReturn(true);
        when(userService.userExistsByUsername(user1.getUsername())).thenReturn(true);
        when(userService.findById(user1.getId())).thenReturn(user1);
        when(userService.findByUsername(user1.getUsername())).thenReturn(user1);
        when(notificationService.getNotifications(user1)).thenReturn(List.of(notification));

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        List<Notification> result = notificationController.getNotifications(authentication);
        assertEquals(1, result.size());
        assertEquals(notification, result.get(0));
    }

    @Test
    public void testGetNotificationsWithInvalidUserId() {
        when(userService.userExists(user1.getId())).thenReturn(false);
        when(userService.userExistsByUsername(user1.getUsername())).thenReturn(false);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        List<Notification> result = notificationController.getNotifications(authentication);
        assertNull(result);
    }

    @Test
    public void testGetNotificationsWithEmptyAuthentication() {
        List<Notification> result = notificationController.getNotifications(null);
        assertEquals(result, new ArrayList<>());
    }

    @Test
    public void testMarkAsRead() {
        when(notificationService.notificationExists(notification.getId())).thenReturn(true);
        when(notificationService.markAsRead(notification)).thenReturn(true);
        when(notificationService.findById(notification.getId())).thenReturn(notification);

        NotificationMarkAsReadRequest request = new NotificationMarkAsReadRequest();
        request.notificationId = notification.getId();

        boolean result = notificationController.markAsRead(request);
        assertTrue(result);
    }

    @Test
    public void testMarkAsReadWithInvalidNotificationId() {
        when(notificationService.notificationExists(notification.getId())).thenReturn(false);

        NotificationMarkAsReadRequest request = new NotificationMarkAsReadRequest();
        request.notificationId = notification.getId();

        boolean result = notificationController.markAsRead(request);
        assertFalse(result);
    }
    
    @Test
    public void testMarkAsReadWithEmptyNotificationId() {
        NotificationMarkAsReadRequest request = new NotificationMarkAsReadRequest();
        request.notificationId = "";

        boolean result = notificationController.markAsRead(request);
        assertFalse(result);
    }
    
    @Test
    public void testMarkAsUnread() {
        when(notificationService.notificationExists(notification.getId())).thenReturn(true);
        when(notificationService.markAsUnread(notification)).thenReturn(true);
        when(notificationService.findById(notification.getId())).thenReturn(notification);

        boolean result = notificationController.markAsUnread(notification.getId());
        assertTrue(result);
    }

    @Test
    public void testMarkAsUnreadWithInvalidNotificationId() {
        when(notificationService.notificationExists(notification.getId())).thenReturn(false);

        boolean result = notificationController.markAsUnread(notification.getId());
        assertFalse(result);
    }
    
    @Test
    public void testMarkAsUnreadWithEmptyNotificationId() {
        boolean result = notificationController.markAsUnread("");
        assertFalse(result);
    }
    
    @Test
    public void testDeleteNotification() {
        when(notificationService.notificationExists(notification.getId())).thenReturn(true);
        when(notificationService.deleteNotification(notification)).thenReturn(true);
        when(notificationService.findById(notification.getId())).thenReturn(notification);

        boolean result = notificationController.deleteNotification(notification.getId());
        assertTrue(result);
    }

    @Test
    public void testDeleteNotificationWithInvalidNotificationId() {
        when(notificationService.notificationExists(notification.getId())).thenReturn(false);

        boolean result = notificationController.deleteNotification(notification.getId());
        assertFalse(result);
    }
    
    @Test
    public void testDeleteNotificationWithEmptyNotificationId() {
        boolean result = notificationController.deleteNotification("");
        assertFalse(result);
    }
    
    @Test
    public void testDeleteAllNotifications() {
        when(userService.userExists(user1.getId())).thenReturn(true);
        when(userService.userExistsByUsername(user1.getUsername())).thenReturn(true);
        when(userService.findById(user1.getId())).thenReturn(user1);
        when(userService.findByUsername(user1.getUsername())).thenReturn(user1);
        when(notificationService.deleteAllNotifications(user1)).thenReturn(true);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        boolean result = notificationController.deleteAllNotifications(authentication);
        assertTrue(result);
    }

    @Test
    public void testDeleteAllNotificationsWithInvalidUserId() {
        when(userService.userExists(user1.getId())).thenReturn(false);
        when(userService.userExistsByUsername(user1.getUsername())).thenReturn(false);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        boolean result = notificationController.deleteAllNotifications(authentication);
        assertFalse(result);
    }

    @Test
    public void testDeleteAllNotificationsWithEmptyAuthentication() {
        boolean result = notificationController.deleteAllNotifications(null);
        assertFalse(result);
    }
    
}
