package example.flashchat.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import example.flashchat.models.Chat;
import example.flashchat.models.Message;
import example.flashchat.models.User;
import example.flashchat.services.ChatService;
import example.flashchat.services.MessageService;
import example.flashchat.services.UserService;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @PostMapping
    public boolean createMessage(@RequestBody MessageRequest request) {
        String chatId = request.chatId;
        String content = request.content;
        String userId = request.userId;

        if (chatId.isEmpty() || content.isEmpty() || userId.isEmpty()) {
            // Empty fields check
            return false;
        }

        if (!chatService.chatExists(chatId) || !userService.userExists(userId)) {
            // Chat or user not found
            return false;
        }

        Chat chat = chatService.getChat(chatId);
        User user = userService.findById(userId);

        if (!chat.getUser1().getId().equals(userId) && !chat.getUser2().getId().equals(userId)) {
            // User not part of the chat
            return false;
        }

        Message message = new Message();
        message.setChat(chat);
        message.setContent(content);
        message.setSender(user);

        return messageService.createMessage(message);
    }


    @GetMapping
    public List<Message> getMessagesByChatId(@RequestParam String chatId, @RequestParam String userId) {
        if (chatId.isEmpty() || userId.isEmpty()) {
            // Empty fields check
            return null;
        }

        if (!chatService.chatExists(chatId) || !userService.userExists(userId)) {
            // Chat or user not found
            return null;
        }

        Chat chat = chatService.getChat(chatId);

        if (!chat.getUser1().getId().equals(userId) && !chat.getUser2().getId().equals(userId)) {
            // User not part of the chat
            return null;
        }

        return messageService.getMessagesByChatId(chatId);
    }
}


class MessageRequest {
    public String chatId;
    public String content;
    public String userId;
}