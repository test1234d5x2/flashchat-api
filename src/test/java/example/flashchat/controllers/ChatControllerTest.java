package example.flashchat.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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

    private final String AUTHENTICATED_USER_USERNAME = "testuser1";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser1 = new User();
        testUser1.setUsername(AUTHENTICATED_USER_USERNAME);
        testUser1.setPassword("password1");

        testUser2 = new User();
        testUser2.setUsername("testuser2");
        testUser2.setPassword("password2");

        testChat = new Chat();
        testChat.setUser1(testUser1);
        testChat.setUser2(testUser2);
    }

    private Authentication createMockAuthentication(String username, boolean authenticated) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        return authentication;
    }


    @Test
    public void testCreateChat() {
        when(chatService.createChat(any(Chat.class))).thenReturn(true);
        when(chatService.chatExists(testUser1, testUser2)).thenReturn(false);
        when(userService.userExists(testUser1.getId())).thenReturn(true);
        when(userService.userExists(testUser2.getId())).thenReturn(true);
        when(userService.userExistsByUsername(testUser1.getUsername())).thenReturn(true);
        when(userService.userExistsByUsername(testUser2.getUsername())).thenReturn(true);
        when(userService.findById(testUser1.getId())).thenReturn(testUser1);
        when(userService.findById(testUser2.getId())).thenReturn(testUser2);
        when(userService.findByUsername(testUser1.getUsername())).thenReturn(testUser1);
        when(userService.findByUsername(testUser2.getUsername())).thenReturn(testUser2);

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.otherParticipantId = testUser2.getId();

        Authentication a = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        assertTrue(chatController.createChat(a, chatRequest));
    }

    @Test
    public void testCreateChatAlreadyExists() {
        when(chatService.chatExists(testUser1, testUser2)).thenReturn(true);

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.otherParticipantId = testUser2.getId();

        Authentication a = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        assertFalse(chatController.createChat(a, chatRequest));
    }

    @Test
    public void testCreateChatInvalidUser1() {
        when(userService.findById(testUser2.getId())).thenReturn(null);
        
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.otherParticipantId = testUser2.getId();

        assertFalse(chatController.createChat(null, chatRequest));
    }

    @Test
    public void testCreateChatInvalidUser2() {
        when(userService.findById(testUser2.getId())).thenReturn(null);
        
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.otherParticipantId = testUser2.getId();

        Authentication a = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        assertFalse(chatController.createChat(a, chatRequest));
    }

    @Test
    public void testCreateChatUsersTheSame() {
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.otherParticipantId = testUser1.getId();

        Authentication a = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        assertFalse(chatController.createChat(a, chatRequest));
    }

    @Test
    public void testCreateChatEmptyUser2() {
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.otherParticipantId = testUser2.getId();

        Authentication a = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        assertFalse(chatController.createChat(a, chatRequest));
    }

    @Test
    public void testGetChats() {
        when(userService.findByUsername(testUser1.getUsername())).thenReturn(testUser1);
        when(userService.userExistsByUsername(testUser1.getUsername())).thenReturn(true);

        Authentication a = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        assertEquals(chatController.getChats(a), new ArrayList<>());
    }

    @Test
    public void testGetChatsInvalidUser() {
        when(userService.userExistsByUsername(testUser1.getUsername())).thenReturn(false);
        Authentication a = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        assertNull(chatController.getChats(a));
    }

    @Test
    public void testGetChatsEmptyUser() {
        assertEquals(chatController.getChats(null), new ArrayList<>());
    }

    @Test
    public void testDeleteChat() {
        when(chatService.deleteChat(testChat.getId())).thenReturn(true);
        when(userService.userExistsByUsername(testUser1.getUsername())).thenReturn(true);
        when(userService.findByUsername(testUser1.getUsername())).thenReturn(testUser1);
        when(chatService.chatExists(testChat.getId())).thenReturn(true);
        when(chatService.getChat(testChat.getId())).thenReturn(testChat);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        DeleteChatRequest request = new DeleteChatRequest();
        request.chatId = testChat.getId();

        assertTrue(chatController.deleteChat(authentication, request));
    }

    @Test
    public void testDeleteChatInvalidUser() {
        when(userService.userExists(testUser1.getId())).thenReturn(false);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        DeleteChatRequest request = new DeleteChatRequest();
        request.chatId = testChat.getId();
        
        assertFalse(chatController.deleteChat(authentication, request));
    }

    @Test
    public void testDeleteChatInvalidChat() {
        when(chatService.chatExists(testChat.getId())).thenReturn(false);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        DeleteChatRequest request = new DeleteChatRequest();
        request.chatId = testChat.getId();

        assertFalse(chatController.deleteChat(authentication, request));
    }

    @Test
    public void testDeleteChatInvalidUserAndChat() {
        when(userService.userExists(testUser1.getId())).thenReturn(false);
        when(chatService.chatExists(testChat.getId())).thenReturn(false);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        DeleteChatRequest request = new DeleteChatRequest();
        request.chatId = testChat.getId();

        assertFalse(chatController.deleteChat(authentication, request));
    }

    @Test
    public void testDeleteChatNotParticipant() {
        when(chatService.deleteChat(testChat.getId())).thenReturn(true);

        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        DeleteChatRequest request = new DeleteChatRequest();
        request.chatId = testChat.getId();

        assertFalse(chatController.deleteChat(authentication, request));
    }

    @Test
    public void testDeleteChatEmptyChat() {
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        DeleteChatRequest request = new DeleteChatRequest();
        request.chatId = testChat.getId();

        assertFalse(chatController.deleteChat(authentication, request));
    }

    @Test
    public void testDeleteChatEmptyUser() {
        Authentication authentication = createMockAuthentication(AUTHENTICATED_USER_USERNAME, false);

        DeleteChatRequest request = new DeleteChatRequest();
        request.chatId = testChat.getId();

        assertFalse(chatController.deleteChat(authentication, request));
    }
}
