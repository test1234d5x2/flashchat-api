package example.flashchat.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTUtil {
    
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration.minutes}")
    private long expirationMinutes; // in minutes

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getExpiration()
            .before(new java.util.Date());
    }

    public boolean validateToken(String token, String username) {
        String extractedUsername = getUsernameFromToken(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    public String generateToken(String username) {
        return Jwts.builder()
            .setSubject(username)
            .signWith(key())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + (expirationMinutes * 60 * 1000)))
            .compact();
    }
}
