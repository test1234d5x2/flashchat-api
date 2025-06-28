package example.flashchat.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import example.flashchat.models.Chat;
import example.flashchat.models.User;
import example.flashchat.services.ChatService;
import example.flashchat.services.UserService;

@RestController
@RequestMapping("/api/v1/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;
    
    @Autowired
    private UserService userService;

    @PostMapping
    public boolean createChat(@RequestParam String participantId1, @RequestParam String participantId2) {
        if (participantId1.isEmpty() || participantId2.isEmpty()) {
            // Check if the participants are empty
            return false;
        }

        if (participantId1.equals(participantId2)) {
            // Check if the participants are the same
            return false;
        }

        if (!userService.userExists(participantId1) || !userService.userExists(participantId2)) {
            // Check if the participants are not found
            return false;
        }

        User participantOne = userService.findById(participantId1);
        User participantTwo = userService.findById(participantId2);

        if (chatService.chatExists(participantOne, participantTwo)) {
            // Check if the chat already exists
            return false;
        }

        Chat chat = new Chat();
        chat.setUser1(participantOne);
        chat.setUser2(participantTwo);
        return chatService.createChat(chat);
    }

    @GetMapping
    public List<Chat> getChats(@RequestParam String userId) {
        if (userId.isEmpty()) {
            // Check if the user id is empty
            return null;
        }

        if (!userService.userExists(userId)) {
            // Check if the user is not found
            return null;
        }

        User user = userService.findById(userId);
        List<Chat> chats = user.getChatsAsUser1();
        chats.addAll(user.getChatsAsUser2());
        return chats;
    }


    @DeleteMapping("/{chatId}")
    public boolean deleteChat(@PathVariable String chatId, @RequestParam String userId) {
        if (userId.isEmpty() || chatId.isEmpty()) {
            // Check if the user id or chat id is empty
            return false;
        }

        if (!chatService.chatExists(chatId)) {
            // Check if the chat is not found
            return false;
        }

        if (!userService.userExists(userId)) {
            // Check if the user is not found
            return false;
        }

        Chat chat = chatService.getChat(chatId);
        if (!chat.getUser1().getId().equals(userId) && !chat.getUser2().getId().equals(userId)) {
            // Check if the user is not a participant of the chat
            return false;
        }

        return chatService.deleteChat(chatId);
    }
}
