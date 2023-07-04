package me.universi.user.services;

import io.jsonwebtoken.*;
import me.universi.user.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;

@Service
public class JWTService {
    @Value("${jwt.secret.key}")
    private String SECRET_KEY;
    @Autowired
    private UserService userService;

    public String buildTokenForUser(User user) {
        String token = null;
        try {
            token = Jwts.builder()
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24h
                    .claim("user", user.getUsername())
                    .signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes("UTF-8"))
                    .compact();
        } catch (Exception e) {
            token = null;
        }
        return token;
    }

    private SigningKeyResolver signingKeyResolver = new SigningKeyResolverAdapter() {
        @Override
        public byte[] resolveSigningKeyBytes(JwsHeader header, Claims claims) {
            try {
                return SECRET_KEY.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }
    };

    public User getUserFromToken(String token) throws Exception {
        Jws<Claims> jws = Jwts.parser().setSigningKeyResolver(signingKeyResolver).parseClaimsJws(token);
        // If integrity checks don't throw continue
        String userName = (String) jws.getBody().get("user");
        // TODO: CACHE USER, INSTEAD OF FIND IN DB
        return (User) userService.loadUserByUsername(userName);
    }
}
