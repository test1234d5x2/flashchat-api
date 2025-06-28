package example.flashchat.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import example.flashchat.models.Chat;
import example.flashchat.models.User;
import example.flashchat.repositories.ChatRepo;

@Service
public class ChatService {

    @Autowired
    private ChatRepo chatRepo;

    public boolean createChat(Chat chat) {
        chatRepo.save(chat);
        return true;
    }

    public List<Chat> getChats(String userId) {
        return chatRepo.findByUser1IdOrUser2Id(userId, userId);
    }

    public Chat getChat(String chatId) {
        return chatRepo.findById(chatId).orElse(null);
    }

    public boolean chatExists(String chatId) {
        return chatRepo.existsById(chatId);
    }

    public boolean chatExists(User user1, User user2) {
        return chatRepo.findByUser1IdAndUser2Id(user1.getId(), user2.getId()).isPresent() || chatRepo.findByUser1IdAndUser2Id(user2.getId(), user1.getId()).isPresent();
    }

    public boolean deleteChat(String chatId) {
        chatRepo.deleteById(chatId);
        return true;
    }
}
