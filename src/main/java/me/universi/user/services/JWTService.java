package me.universi.user.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JWTService {
    @Value("${jwt.secret.key}")
    private String SECRET_KEY;


}
