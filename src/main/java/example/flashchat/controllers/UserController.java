package example.flashchat.controllers;

import example.flashchat.models.User;
import example.flashchat.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @DeleteMapping
    public boolean deleteUser(@RequestParam String userId) {
        return userService.deleteUser(userId);
    }


    @GetMapping("/details/{userId}")
    public User getUserDetails(@PathVariable String userId) {
        if (userId.isEmpty()) {
            return null;
        }

        if (!userService.userExists(userId)) {
            return null;
        }

        return userService.findById(userId);
    }

    @GetMapping("/search/{searchQuery}")
    public List<User> searchUsersGet(@PathVariable String searchQuery) {
        if (searchQuery.isEmpty()) {
            return List.of();
        }

        return userService.searchUsers(searchQuery);
    }
}