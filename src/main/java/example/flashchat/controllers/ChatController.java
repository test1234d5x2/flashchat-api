package example.flashchat.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public boolean createChat(Authentication authentication, @RequestBody ChatRequest request) {
        String otherParticipantUserId = request.otherParticipantId;

        if (authentication == null) {
            System.out.println("No authentication present");
            return false;
        }

        String username = authentication.getName().toString();

        if (otherParticipantUserId.isEmpty()) {
            // Check if the participants are empty
            return false;
        }

        if (!userService.userExists(otherParticipantUserId) || !userService.userExistsByUsername(username)) {
            // Check if the participants are not found
            return false;
        }

        User participantOne = userService.findByUsername(username);
        User participantTwo = userService.findById(otherParticipantUserId);

        if (participantOne.equals(participantTwo)) {
            // Check if they are the same user.
            return false;
        }

        if (chatService.chatExists(participantOne, participantTwo)) {
            // Check if the chat already exists
            return false;
        }

        Chat chat = new Chat();
        chat.setUser1(participantOne);
        chat.setUser2(participantTwo);
        return chatService.createChat(chat);
    }

    @GetMapping("/myChats")
    public List<Chat> getChats(Authentication authentication) {
        if (authentication == null) {
            System.out.println("No authentication present");
            return new ArrayList<>();
        }

        String username = authentication.getName().toString();

        if (!userService.userExistsByUsername(username)) {
            // Check if the user is not found
            return null;
        }

        User user = userService.findByUsername(username);
        List<Chat> chats = user.getChatsAsUser1();
        chats.addAll(user.getChatsAsUser2());
        return chats;
    }

    @GetMapping("/chatId/{chatId}")
    public Chat getChatViaId(Authentication authentication, @PathVariable String chatId) {
        if (authentication == null) {
            System.out.println("No authentication present");
            return null;
        }

        String username = authentication.getName().toString();

        if (!userService.userExistsByUsername(username)) {
            // Check if user exists.
            return null;
        }

        if (chatId.isEmpty()) {
            // Check if the chat id is empty
            return null;
        }

        if (!chatService.chatExists(chatId)) {
            // Check if the chat is not found
            return null;
        }

        // Check that one of the participants is calling this API endpoint otherwise do not accept.

        System.out.println(chatService.getChat(chatId));
        return chatService.getChat(chatId);
    }

    // @PostMapping("/getChat")
    // public Chat getChat(@RequestParam String user1Id, @RequestParam String user2Id) {
    //     if (user1Id.isEmpty() || user2Id.isEmpty()) {
    //         // Check if the user ids are empty
    //         return null;
    //     }

    //     if (!userService.userExists(user1Id) || !userService.userExists(user2Id)) {
    //         // Check if the users are not found
    //         return null;
    //     }

    //     User user1 = userService.findById(user1Id);
    //     User user2 = userService.findById(user2Id);

    //     return chatService.getChat(user1, user2);
    // }

    @PostMapping("/getChat")
    public Chat getChat(Authentication authentication, @RequestBody ChatRequest request) {
        if (authentication == null) {
            System.out.println("No authentication present");
            return null;
        }

        String username = authentication.getName().toString();
        String otherParticipantId = request.otherParticipantId;

        if (otherParticipantId.isEmpty()) {
            // Check if the user ids are empty
            return null;
        }

        if (!userService.userExists(otherParticipantId) || !userService.userExistsByUsername(username)) {
            // Check if the users are not found
            return null;
        }

        User user1 = userService.findByUsername(username);
        User user2 = userService.findById(otherParticipantId);

        if (user1.equals(user2)) {
            // Check if the users are the same.
            return null;
        }

        if (!chatService.chatExists(user1, user2)) {
            // If the chat does not exist, create it
            ChatRequest r = new ChatRequest();
            r.otherParticipantId = otherParticipantId;
            if (!createChat(authentication, r)) {
                return null;
            }
        }

        return chatService.getChat(user1, user2);
    }

    @DeleteMapping("/deleteChat")
    public boolean deleteChat(Authentication authentication, @RequestBody DeleteChatRequest request) {
        if (authentication == null) {
            System.out.println("No authentication present");
            return false;
        }

        String username = authentication.getName().toString();
        String chatId = request.chatId;

        if (chatId.isEmpty()) {
            // Check if the user id or chat id is empty
            return false;
        }

        if (!chatService.chatExists(chatId)) {
            // Check if the chat is not found
            return false;
        }

        if (!userService.userExistsByUsername(username)) {
            // Check if the user is not found
            return false;
        }

        Chat chat = chatService.getChat(chatId);
        User user = userService.findByUsername(username);
        if (!chat.getUser1().equals(user) && !chat.getUser2().equals(user)) {
            // Check if the user is not a participant of the chat
            return false;
        }

        return chatService.deleteChat(chatId);
    }
}


class ChatRequest {
    public String otherParticipantId;
}

class DeleteChatRequest {
    public String chatId;
}