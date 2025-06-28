package example.flashchat.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import example.flashchat.repositories.MessageRepo;

public class MessageServiceTest {
    @Mock
    private MessageRepo messageRepo;

    @InjectMocks
    private MessageService messageService;

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

    @Test
    public void testCreateMessage() {
        when(messageRepo.save(testMessage)).thenReturn(testMessage);
        assertTrue(messageService.createMessage(testMessage));
    }

    @Test
    public void testGetMessagesByChatId() {
        when(messageRepo.findByChatId(testChat.getId())).thenReturn(List.of(testMessage));
        List<Message> messages = messageService.getMessagesByChatId(testChat.getId());
        assertEquals(1, messages.size());
        assertEquals(testMessage, messages.get(0));
    }
}
