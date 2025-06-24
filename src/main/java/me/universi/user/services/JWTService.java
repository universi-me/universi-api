package me.universi.user.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import me.universi.user.entities.User;
import me.universi.user.exceptions.UserException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JWTService {

    @Value("${jwt.enabled}")
    public boolean ENABLED;

    @Value("${jwt.secret.key}")
    private String secretKeyString;

    @Value("${jwt.expiration}")
    public long EXPIRATION; // in seconds

    public static final String issuer = "universi.me";

    private SecretKey secretKey;
    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        if (!ENABLED) return;

        byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 64) {
            throw new IllegalArgumentException("JWT secret key must be at least 64 bytes for HS512");
        }

        this.secretKey = Keys.hmacShaKeyFor(keyBytes);

        this.jwtParser = Jwts.parser()
                .setSigningKey(secretKey)
                .build();
    }

    public String buildTokenForUser(User user) {
        if (!ENABLED) return null;

        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + (EXPIRATION * 1000) ); // convert to ms

        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(user.getId().toString())
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public User getUserFromToken(String token) {
        if (!ENABLED) return null;

        Claims claims;

        try {
            claims = jwtParser.parseClaimsJws(token).getBody();
        } catch (Exception e) {
            throw new UserException("Invalid JWT token");
        }

        if (claims.getExpiration().before(new Date())) {
            throw new UserException("JWT Token Expired");
        }

        String userId = claims.getSubject();
        return UserService.getInstance()
                .findUnchecked(UUID.fromString(userId))
                .orElseThrow(() -> new UserException("User not found for JWT token"));
    }
}
