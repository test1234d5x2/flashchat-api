package example.flashchat.services;

import example.flashchat.models.LoginDetails;
import example.flashchat.models.User;
import example.flashchat.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
            return false;
        }
    }

    public boolean login(LoginDetails loginDetails) {
        return userRepo.findByUsernameAndPassword(loginDetails.getUsername(), loginDetails.getPassword()).isEmpty();
    }

    public boolean deleteUser(UUID id) {
        if (userRepo.findById(id).isEmpty()) {
            return false;
        }

        userRepo.deleteById(id);
        return true;
    }
}
