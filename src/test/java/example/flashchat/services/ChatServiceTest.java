package example.flashchat.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import example.flashchat.models.Chat;
import example.flashchat.models.User;
import example.flashchat.repositories.ChatRepo;

public class ChatServiceTest {
    @Mock
    private ChatRepo chatRepo;

    @InjectMocks
    private ChatService chatService;

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
        when(chatRepo.save(testChat)).thenReturn(testChat);
        assertTrue(chatService.createChat(testChat));
    }

    @Test
    public void testDeleteChat() {
        assertTrue(chatService.deleteChat(testChat.getId()));
    }

    @Test
    public void testChatExists() {
        when(chatRepo.findByUser1IdAndUser2Id(testUser1.getId(), testUser2.getId())).thenReturn(Optional.of(testChat));
        assertTrue(chatService.chatExists(testUser1, testUser2));
    }

    @Test
    public void testGetChats() {
        when(chatRepo.findByUser1IdOrUser2Id(testUser1.getId(), testUser1.getId())).thenReturn(List.of(testChat));
        List<Chat> chats = chatService.getChats(testUser1.getId());
        assertEquals(chats.size(), 1);
        assertEquals(chats.get(0), testChat);
    }

    @Test
    public void testGetChat() {
        when(chatRepo.findById(testChat.getId())).thenReturn(Optional.of(testChat));
        Chat chat = chatService.getChat(testChat.getId());
        assertEquals(chat, testChat);
    }
}
