package example.flashchat.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");

        testChat = new Chat();
        testChat.setUser1(testUser);
        testChat.setUser2(testUser);

        testMessage = new Message();
        testMessage.setChat(testChat);
        testMessage.setSender(testUser);
        testMessage.setContent("test message");
    }

    private MessageRequest createMessageRequest(String chatId, String content, String userId) {
        MessageRequest request = new MessageRequest();
        request.chatId = chatId;
        request.content = content;
        request.userId = userId;
        return request;
    }

    @Test
    public void testCreateMessage() {
        when(messageService.createMessage(any(Message.class))).thenReturn(true);
        when(chatService.chatExists(testChat.getId())).thenReturn(true);
        when(chatService.getChat(testChat.getId())).thenReturn(testChat);
        when(userService.userExists(testUser.getId())).thenReturn(true);
        when(userService.findById(testUser.getId())).thenReturn(testUser);
        when(notificationService.createNotification(any(Notification.class))).thenReturn(true);
        assertTrue(messageController.createMessage(createMessageRequest(testChat.getId(), testMessage.getContent(), testUser.getId())));
    }

    @Test
    public void testCreateMessageUserNotPartOfChat() {
        when(messageService.createMessage(testMessage)).thenReturn(true);
        assertFalse(messageController.createMessage(createMessageRequest(testChat.getId(), testMessage.getContent(), "not_part_of_chat")));
    }

    @Test
    public void testCreateMessageChatNotFound() {
        when(chatService.chatExists("not_found")).thenReturn(false);
        assertFalse(messageController.createMessage(createMessageRequest("not_found", testMessage.getContent(), testUser.getId())));
    }

    @Test
    public void testCreateMessageUserNotFound() {
        when(userService.userExists("not_found")).thenReturn(false);
        assertFalse(messageController.createMessage(createMessageRequest(testChat.getId(), testMessage.getContent(), "not_found")));
    }

    @Test
    public void testCreateMessageEmptyChatId() {
        assertFalse(messageController.createMessage(createMessageRequest("", testMessage.getContent(), testUser.getId())));
    }

    @Test
    public void testCreateMessageEmptyContent() {
        assertFalse(messageController.createMessage(createMessageRequest(testChat.getId(), "", testUser.getId())));
    }

    @Test
    public void testCreateMessageEmptyUserId() {
        assertFalse(messageController.createMessage(createMessageRequest(testChat.getId(), testMessage.getContent(), "")));
    }

    @Test
    public void testGetMessagesByChatId() {
        when(messageService.getMessagesByChatId(testChat.getId())).thenReturn(List.of(testMessage));
        when(chatService.chatExists(testChat.getId())).thenReturn(true);
        when(chatService.getChat(testChat.getId())).thenReturn(testChat);
        when(userService.userExists(testUser.getId())).thenReturn(true);
        List<Message> messages = messageController.getMessagesByChatId(testChat.getId(), testUser.getId());
        assertEquals(1, messages.size());
        assertEquals(testMessage, messages.get(0));
    }

    @Test
    public void testGetMessagesByChatIdUserNotPartOfChat() {
        when(messageService.getMessagesByChatId(testChat.getId())).thenReturn(List.of(testMessage));
        assertNull(messageController.getMessagesByChatId(testChat.getId(), "not_part_of_chat"));
    }

    @Test
    public void testGetMessagesByChatIdChatNotFound() {
        when(chatService.chatExists("not_found")).thenReturn(false);
        assertNull(messageController.getMessagesByChatId("not_found", testUser.getId()));
    }

    @Test
    public void testGetMessagesByChatIdUserNotFound() {
        when(userService.userExists("not_found")).thenReturn(false);
        assertNull(messageController.getMessagesByChatId(testChat.getId(), "not_found"));
    }

    @Test
    public void testGetMessagesByChatIdEmptyChatId() {
        assertNull(messageController.getMessagesByChatId("", testUser.getId()));
    }

    @Test
    public void testGetMessagesByChatIdEmptyUserId() {
        assertNull(messageController.getMessagesByChatId(testChat.getId(), ""));
    }
}
