package example.flashchat.controllers;

import example.flashchat.models.LoginDetails;
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

    @PostMapping
    public boolean createUser(@RequestBody LoginDetails loginDetails) {

        if (loginDetails.getUsername().isEmpty() || loginDetails.getPassword().isEmpty()) {
            return false;
        }

        User u = new User();
        u.setUsername(loginDetails.getUsername());
        u.setPassword(loginDetails.getPassword());

        if (userService.userExists(u.getUsername())) {
            return false;
        }

        return userService.createUser(u);
    }

    @PostMapping("/login")
    public boolean login(@RequestBody LoginDetails loginDetails) {
        return userService.login(loginDetails);
    }

    @DeleteMapping
    public boolean deleteUser(@RequestParam String userId) {
        return userService.deleteUser(userId);
    }

    @GetMapping("/search/{searchQuery}")
    public List<User> searchUsersGet(@PathVariable String searchQuery) {
        if (searchQuery.isEmpty()) {
            return List.of();
        }

        return userService.searchUsers(searchQuery);
    } // TODO: Needs testing.
}