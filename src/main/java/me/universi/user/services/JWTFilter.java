package me.universi.user.services;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.universi.user.entities.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    public JWTFilter(JWTService jwtService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        
        // prefer check first if user is logged-in on session, else use JWT token to authenticate
        if(!jwtService.ENABLED || header == null || !header.startsWith("Bearer ") || LoginService.getInstance().userIsLoggedIn()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            User user = jwtService.getUserFromToken(header.replace("Bearer ", ""));
            LoginService.getInstance().configureSessionForUser(user, authenticationManager);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
