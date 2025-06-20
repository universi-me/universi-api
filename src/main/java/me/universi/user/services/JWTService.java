package me.universi.user.services;

import io.jsonwebtoken.*;
import me.universi.user.entities.User;
import me.universi.user.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;

@Service
public class JWTService {
    @Value("${jwt.enabled}")
    public boolean ENABLED;
    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    public JWTService() {

    }

    public String buildTokenForUser(User user) {
        if(!ENABLED) {
            return null;
        }
        String token = null;
        try {
            long timeNow = System.currentTimeMillis();
            token = Jwts.builder()
                    .setIssuer("universi.me")
                    .setIssuedAt(new Date(timeNow))
                    .setExpiration(new Date(timeNow + 86400000)) // 24h
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
        Jws<Claims> jws = Jwts.parser().setSigningKeyResolver(signingKeyResolver).build().parseSignedClaims(token);
        // If integrity checks don't throw continue
        if(jws.getBody().getExpiration().before(new Date())) {
            throw new UserException("JWT Token Expired");
        }

        String userName = (String) jws.getBody().get("user");
        // TODO: CACHE USER, INSTEAD OF FIND IN DB
        return (User) UserService.getInstance().loadUserByUsername(userName);
    }
}
