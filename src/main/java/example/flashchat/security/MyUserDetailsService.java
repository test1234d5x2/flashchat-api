package example.flashchat.security;

import example.flashchat.models.User;
import example.flashchat.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

public class MyUserDetailsService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepo userRepo;

    public MyUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new org.springframework.security.core.userdetails.User(
                u.getUsername(),
                u.getPassword(),
                true,
                true,
                true,
                true,
                Collections.emptyList()
        );
    }

}
