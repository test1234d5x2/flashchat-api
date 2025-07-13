package example.flashchat.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import example.flashchat.models.User;
import example.flashchat.repositories.UserRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTFilter extends OncePerRequestFilter {
    
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JWTUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/api/v1/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException  {
        try {
            String token = parseHeader(request);
            if (token != null) {
                String username = jwtUtil.getUsernameFromToken(token);
                Optional<User> userOptional = userRepo.findByUsername(username);
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    if (jwtUtil.validateToken(token, user.getUsername())) {
                        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                                user.getUsername(),
                                user.getPassword(),
                                new ArrayList<>()
                        );
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(user);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } 
                    else {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                    }
                } 
                else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                }
            }
        } 
        catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
        }

        filterChain.doFilter(request, response);
    }

    private String parseHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
