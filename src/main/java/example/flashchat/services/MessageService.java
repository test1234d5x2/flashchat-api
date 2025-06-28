package example.flashchat.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import example.flashchat.models.Message;
import example.flashchat.repositories.MessageRepo;

@Service
public class MessageService {

    @Autowired
    private MessageRepo messageRepo;
    
    public boolean createMessage(Message message) {
        messageRepo.save(message);
        return true;
    }

    public List<Message> getMessagesByChatId(String chatId) {
        return messageRepo.findByChatId(chatId);
    }
}
