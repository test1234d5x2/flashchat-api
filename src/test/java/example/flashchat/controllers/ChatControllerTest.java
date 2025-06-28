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
import example.flashchat.models.User;
import example.flashchat.services.ChatService;
import example.flashchat.services.UserService;

public class ChatControllerTest {
    @Mock
    private ChatService chatService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ChatController chatController;

    private Chat testChat;
    private User testUser1;
    private User testUser2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser1 = new User();
        testUser1.setUsername("testuser1");
        testUser1.setPassword("password1");

        testUser2 = new User();
        testUser2.setUsername("testuser2");
        testUser2.setPassword("password2");

        testChat = new Chat();
        testChat.setUser1(testUser1);
        testChat.setUser2(testUser2);
    }

    @Test
    public void testCreateChat() {
        when(chatService.createChat(any(Chat.class))).thenReturn(true);
        when(chatService.chatExists(testUser1, testUser2)).thenReturn(false);
        when(userService.userExists(testUser1.getId())).thenReturn(true);
        when(userService.userExists(testUser2.getId())).thenReturn(true);
        when(userService.findById(testUser1.getId())).thenReturn(testUser1);
        when(userService.findById(testUser2.getId())).thenReturn(testUser2);
        assertTrue(chatController.createChat(testUser1.getId(), testUser2.getId()));
    }

    @Test
    public void testCreateChatAlreadyExists() {
        when(chatService.chatExists(testUser1, testUser2)).thenReturn(true);
        assertFalse(chatController.createChat(testUser1.getId(), testUser2.getId()));
    }

    @Test
    public void testCreateChatInvalidUser1() {
        when(userService.findById(testUser1.getId())).thenReturn(null);
        assertFalse(chatController.createChat(testUser1.getId(), testUser2.getId()));
    }

    @Test
    public void testCreateChatInvalidUser2() {
        when(userService.findById(testUser2.getId())).thenReturn(null);
        assertFalse(chatController.createChat(testUser1.getId(), testUser2.getId()));
    }

    @Test
    public void testCreateChatUsersTheSame() {
        assertFalse(chatController.createChat(testUser1.getId(), testUser1.getId()));
    }

    @Test
    public void testCreateChatEmptyUser1() {
        assertFalse(chatController.createChat("", testUser2.getId()));
    }

    @Test
    public void testCreateChatEmptyUser2() {
        assertFalse(chatController.createChat(testUser2.getId(), ""));
    }

    @Test
    public void testGetChats() {
        when(userService.findById(testUser1.getId())).thenReturn(testUser1);
        when(userService.userExists(testUser1.getId())).thenReturn(true);
        assertEquals(chatController.getChats(testUser1.getId()), List.of());
    }

    @Test
    public void testGetChatsInvalidUser() {
        when(userService.userExists(testUser1.getId())).thenReturn(false);
        assertNull(chatController.getChats(testUser1.getId()));
    }

    @Test
    public void testGetChatsEmptyUser() {
        assertNull(chatController.getChats(""));
    }

    @Test
    public void testDeleteChat() {
        when(chatService.deleteChat(testChat.getId())).thenReturn(true);
        when(userService.userExists(testUser1.getId())).thenReturn(true);
        when(chatService.chatExists(testChat.getId())).thenReturn(true);
        when(chatService.getChat(testChat.getId())).thenReturn(testChat);
        assertTrue(chatController.deleteChat(testChat.getId(), testUser1.getId()));
    }

    @Test
    public void testDeleteChatInvalidUser() {
        when(userService.userExists(testUser1.getId())).thenReturn(false);
        assertFalse(chatController.deleteChat(testChat.getId(), testUser1.getId()));
    }

    @Test
    public void testDeleteChatInvalidChat() {
        when(chatService.chatExists(testChat.getId())).thenReturn(false);
        assertFalse(chatController.deleteChat(testChat.getId(), testUser1.getId()));
    }

    @Test
    public void testDeleteChatInvalidUserAndChat() {
        when(userService.userExists(testUser1.getId())).thenReturn(false);
        when(chatService.chatExists(testChat.getId())).thenReturn(false);
        assertFalse(chatController.deleteChat(testChat.getId(), testUser1.getId()));
    }

    @Test
    public void testDeleteChatNotParticipant() {
        when(chatService.deleteChat(testChat.getId())).thenReturn(true);
        assertFalse(chatController.deleteChat(testChat.getId(), ""));
    }

    @Test
    public void testDeleteChatEmptyChat() {
        assertFalse(chatController.deleteChat("", testUser1.getId()));
    }

    @Test
    public void testDeleteChatEmptyUser() {
        assertFalse(chatController.deleteChat(testChat.getId(), ""));
    }
}
