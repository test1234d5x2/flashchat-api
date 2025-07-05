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
    public boolean createUser(@RequestBody UserRequest userRequest) {
        final String username = userRequest.username;
        final String handle = userRequest.handle;
        final String password = userRequest.password;

        if (username.isEmpty() || handle.isEmpty() || password.isEmpty()) {
            return false;
        }

        User u = new User();
        u.setUsername(username);
        u.setPassword(password);
        u.setHandle(handle);

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

class UserRequest {
    public String username;
    public String handle;
    public String password;
}