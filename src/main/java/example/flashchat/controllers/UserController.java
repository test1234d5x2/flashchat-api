package example.flashchat.controllers;

import example.flashchat.models.LoginDetails;
import example.flashchat.models.User;
import example.flashchat.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public boolean createUser(@RequestBody LoginDetails loginDetails) {
        User u = new User();
        u.setUsername(loginDetails.getUsername());
        u.setPassword(loginDetails.getPassword());

        return userService.createUser(u);
    }

    @PostMapping("/login")
    public boolean login(@RequestBody LoginDetails loginDetails) {
        return userService.login(loginDetails);
    }

    @DeleteMapping
    public boolean deleteUser(@RequestBody UUID id) {
        return userService.deleteUser(id);
    }


}
