package me.universi.user.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import me.universi.Sys;
import me.universi.user.entities.User;
import me.universi.user.exceptions.UserException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JWTService {

    @Value("${jwt.secret.key}")
    private String secretKeyString;

    @Value("${jwt.issuer}")
    private String ISSUER;

    @Value("${jwt.expiration}")
    public long EXPIRATION; // in seconds

    private static final String VERSION_DATE = "versionDate";
    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Bearer ";
    public static final String JWT_TOKEN = "JWT_TOKEN";

    private SecretKey secretKey;
    private JwtParser jwtParser;

    public static JWTService getInstance() {
        return Sys.context().getBean("JWTService", JWTService.class);
    }

    @PostConstruct
    public void init() {
        if(secretKeyString != null && !secretKeyString.isEmpty()) {
            byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
            if (keyBytes.length < 64) {
                throw new UserException("JWT secret key specified must be at least 64 bytes for HS512");
            }
            this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        } else {
            try {
                this.secretKey = Jwts.SIG.HS512.key().build();
            } catch (IllegalArgumentException e) {
                throw new UserException("Failed to generate JWT secret key");
            }
        }

        this.jwtParser = Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(ISSUER)
                .build();
    }



    public String buildTokenForUser(User user) {

        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + (EXPIRATION * 1000) ); // convert to ms

        if (user == null || user.getId() == null) {
            throw new UserException("User information is required to build JWT token");
        }

        if(user.getVersionDate() == null) {
            LoginService.getInstance().refreshUserVersionDate(user);
            UserService.getInstance().save(user);
        }

        return Jwts.builder()
                .issuer(ISSUER)
                .subject(user.getId().toString())
                .claim(VERSION_DATE, user.getVersionDate().toString())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public User getUserFromToken(String token) {
        Claims claims;

        try {
            claims = jwtParser.parseSignedClaims(token).getPayload();
        } catch (Exception e) {
            throw new UserException("Invalid JWT token", HttpStatus.UNAUTHORIZED);
        }

        // Check if the token is expired
        if (claims.getExpiration().before(new Date())) {
            throw new UserException("JWT Token Expired", HttpStatus.PRECONDITION_FAILED);
        }

        User user;
        // not found user
        try {
            user = UserService.getInstance()
                    .findUnchecked( UUID.fromString(claims.getSubject()) ).orElseThrow();
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            throw new UserException("User not found for this token", HttpStatus.UNAUTHORIZED);
        }

        // Additional check date time is before (possibility for force logout user remotely)
        String versionDate = claims.get(VERSION_DATE, String.class);
        LocalDateTime versionDateTime = LocalDateTime.parse(versionDate);
        if (user.getVersionDate() == null || user.getVersionDate().isAfter(versionDateTime)) {
            throw new UserException("User version date is not valid for this token", HttpStatus.PRECONDITION_FAILED);
        }

        return user;
    }

    public User getUserFromRequest(HttpServletRequest request) {
        try {

            // 1 verify token authentication from header
            String header = request.getHeader(AUTH_HEADER);
            if (header != null && header.startsWith(AUTHENTICATION_SCHEME)) {
                return getUserFromToken(header.substring(7)); // remove "Bearer " prefix
            }

            // 2 verify token authentication from cookie
            String token = RequestService.getInstance().getCookieValue(JWT_TOKEN);
            if (token != null) {
                return getUserFromToken(token);
            }

        } catch (UserException e) {
            if(e.status != HttpStatus.UNAUTHORIZED) {
                throw e;
            }
        }

        return null;
    }
}
