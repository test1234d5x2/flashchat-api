package example.flashchat.controllers;

import example.flashchat.models.User;
import example.flashchat.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @DeleteMapping
    public boolean deleteUser(Authentication authentication) {
        if (authentication == null) {
            System.out.println("No authentication present");
            return false;
        }

        String username = authentication.getName().toString();

        if (!userService.userExistsByUsername(username)) {
            return false;
        }

        User user = userService.findByUsername(username);
        return userService.deleteUser(user);

    }


    @GetMapping("/details/{userId}")
    public User getUserDetails(Authentication authentication, @PathVariable String userId) {
        if (authentication == null) {
            System.out.println("No authentication present");
            return null;
        }

        if (userId.isEmpty()) {
            return null;
        }

        if (!userService.userExists(userId)) {
            return null;
        }

        return userService.findById(userId);
    }


    @GetMapping("/details/me")
    public User getMyDetails(Authentication authentication) {
        if (authentication == null) {
            System.out.println("No authentication present");
            return null;
        }

        String username = authentication.getName().toString();

        if (!userService.userExistsByUsername(username)) {
            return null;
        }

        return userService.findByUsername(username);
    }

    @GetMapping("/search/{searchQuery}")
    public List<User> searchUsersGet(@PathVariable String searchQuery) {
        if (searchQuery.isEmpty()) {
            return List.of();
        }

        return userService.searchUsers(searchQuery);
    }
}