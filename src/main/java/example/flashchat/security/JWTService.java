package example.flashchat.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import example.flashchat.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;

@Component
public class JWTService {

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.expiration.minutes}")
    private long expirationMinutes;

    private Algorithm algorithm;
    private JWTVerifier verifier;


    public void init() {
        this.algorithm = Algorithm.HMAC512(SECRET);
        this.verifier = JWT.require(algorithm)
                            .withIssuer("flashchat-issuer")
                            .build();
    }

    @jakarta.annotation.PostConstruct
    public void postConstruct() {
        init();
    }

    public String extractUsername(String token) {
        try {
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getSubject();
        }
        catch (JWTVerificationException e) {
            return null;
        }
    }

    public String extractId(String token) {
        try {
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("userId").asString();
        }
        catch (JWTVerificationException e) {
            return null;
        }
    }

    public Date extractExpiration(String token) {
        try {
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getExpiresAt();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    public boolean validateToken(String token, String username, String userId) {
        if (token == null) {
            return false;
        }

        String tokenUsername = extractUsername(token);
        String tokenId = extractId(token);

        if (tokenUsername == null || tokenId == null) {
            return false;
        }

        if (!tokenUsername.equals(username) || !tokenId.equals(userId)) {
            return false;
        }

        return true;
    }


    public String generateToken(User u) {
        return JWT.create()
                .withSubject(u.getUsername())
                .withClaim("userId", u.getId())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationMinutes * 60 * 1000))
                .sign(algorithm);
    }
}
