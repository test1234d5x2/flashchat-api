package example.flashchat.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import example.flashchat.models.User;
import example.flashchat.security.JWTUtil;
import example.flashchat.services.UserService;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtil jwtUtil;


    @PostMapping("/register")
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
    public ResponseEntity<Boolean> login(@RequestBody LoginRequest request) {
        String username = request.username;
        String password = request.password;

        if (username.isEmpty() || password.isEmpty()) {
            return ResponseEntity.internalServerError().body(false);
        }

        if (!userService.login(username, password)) {
            return ResponseEntity.status(404).body(false);
        }

        ResponseCookie jwtCookie = ResponseCookie.from("accessToken", jwtUtil.generateToken(username))
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(3600)
            .sameSite("Lax")
            .domain("localhost")
            .build();

        System.out.println("Attempting to set cookie: " + jwtCookie.toString());
        System.out.println(ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(true));

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(true);
    }
}


class UserRequest {
    public String username;
    public String handle;
    public String password;
}


class LoginRequest {
    public String username;
    public String password;
}