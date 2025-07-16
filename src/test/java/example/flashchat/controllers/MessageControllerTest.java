package example.flashchat.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

import example.flashchat.models.Chat;
import example.flashchat.models.Message;
import example.flashchat.models.User;
import example.flashchat.services.ChatService;
import example.flashchat.services.MessageService;
import example.flashchat.services.UserService;
import example.flashchat.services.NotificationService;
import example.flashchat.models.Notification;

public class MessageControllerTest {
    @Mock
    private MessageService messageService;

    @Mock
    private ChatService chatService;

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private MessageController messageController;

    private Message testMessage;
    private Chat testChat;
    private User testUser;

    private final String AUTHENTICATED_USER_USERNAME = "testuser";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setUsername(AUTHENTICATED_USER_USERNAME);
        testUser.setPassword("password");

        testChat = new Chat();
        testChat.setUser1(testUser);
        testChat.setUser2(testUser);

        testMessage = new Message();
        testMessage.setChat(testChat);
        testMessage.setSender(testUser);
        testMessage.setContent("test message");
    }

    private MessageRequest createMessageRequest(String chatId, String content) {
        MessageRequest request = new MessageRequest();
        request.chatId = chatId;
        request.content = content;
        return request;
    }

    private Authentication createMockAuthentication(String username, boolean authenticated) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        authentication.setAuthenticated(authenticated);
        return authentication;
    }


    @Test
    public void testCreateMessage() {
        when(messageService.createMessage(any(Message.class))).thenReturn(true);
        when(chatService.chatExists(testChat.getId())).thenReturn(true);
        when(chatService.getChat(testChat.getId())).thenReturn(testChat);
        when(userService.userExists(testUser.getId())).thenReturn(true);
        when(userService.userExistsByUsername(AUTHENTICATED_USER_USERNAME)).thenReturn(true);
        when(userService.findById(testUser.getId())).thenReturn(testUser);
        when(userService.findByUsername(AUTHENTICATED_USER_USERNAME)).thenReturn(testUser);
        when(notificationService.createNotification(any(Notification.class))).thenReturn(true);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        assertTrue(messageController.createMessage(authentication, createMessageRequest(testChat.getId(), testMessage.getContent())));
    }

    @Test
    public void testCreateMessageUserNotPartOfChat() {
        when(messageService.createMessage(testMessage)).thenReturn(true);

        Authentication authentication = createMockAuthentication("not_part_of_chat", false);
        assertFalse(messageController.createMessage(authentication, createMessageRequest(testChat.getId(), testMessage.getContent())));
    }

    @Test
    public void testCreateMessageChatNotFound() {
        when(chatService.chatExists("not_found")).thenReturn(false);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);
        assertFalse(messageController.createMessage(authentication, createMessageRequest("not_found", testMessage.getContent())));
    }

    @Test
    public void testCreateMessageEmptyChatId() {
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);
        assertFalse(messageController.createMessage(authentication, createMessageRequest("", testMessage.getContent())));
    }

    @Test
    public void testCreateMessageEmptyContent() {
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);
        assertFalse(messageController.createMessage(authentication, createMessageRequest(testChat.getId(), "")));
    }

    @Test
    public void testGetMessagesByChatId() {
        when(messageService.getMessagesByChatId(testChat.getId())).thenReturn(List.of(testMessage));
        when(chatService.chatExists(testChat.getId())).thenReturn(true);
        when(chatService.getChat(testChat.getId())).thenReturn(testChat);
        when(userService.userExists(testUser.getId())).thenReturn(true);
        when(userService.userExistsByUsername(AUTHENTICATED_USER_USERNAME)).thenReturn(true);
        when(userService.findByUsername(AUTHENTICATED_USER_USERNAME)).thenReturn(testUser);
        
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);
        List<Message> messages = messageController.getMessagesByChatId(authentication, testChat.getId());
        assertEquals(1, messages.size());
        assertEquals(testMessage, messages.get(0));
    }

    @Test
    public void testGetMessagesByChatIdUserNotPartOfChat() {
        when(messageService.getMessagesByChatId(testChat.getId())).thenReturn(List.of(testMessage));

        Authentication authentication = createMockAuthentication("not_part_of_chat", false);
        assertNull(messageController.getMessagesByChatId(authentication, testChat.getId()));
    }

    @Test
    public void testGetMessagesByChatIdChatNotFound() {
        when(chatService.chatExists("not_found")).thenReturn(false);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);
        assertNull(messageController.getMessagesByChatId(authentication, "not_found"));
    }

    @Test
    public void testGetMessagesByChatIdEmptyChatId() {
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);
        assertNull(messageController.getMessagesByChatId(authentication, ""));
    }
}
