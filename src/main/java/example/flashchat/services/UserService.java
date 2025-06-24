package example.flashchat.services;

import example.flashchat.models.LoginDetails;
import example.flashchat.models.User;
import example.flashchat.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean createUser(User user) {
        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
            return false;
        }

        try {

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepo.save(user);
            return true;
        }
        catch (Exception e) {
            // Implement logging.
            System.out.println("OK");
            return false;
        }
    }

    public boolean login(LoginDetails loginDetails) {
        Optional<User> u = userRepo.findByUsername(loginDetails.getUsername());

        if (u.isEmpty()) {
            return false;
        }

        if (!passwordEncoder.matches(loginDetails.getPassword(), u.get().getPassword())) {
            return false;
        }

        return true;
    }

    public boolean deleteUser(String id) {
        if (userRepo.findById(id).isEmpty()) {
            return false;
        }

        userRepo.deleteById(id);
        return true;
    }

    public boolean userExists(String id) {
        return userRepo.findById(id).isPresent();
    }

    public User findById(String id) {
        return userRepo.findById(id).orElse(null);
    }
}
