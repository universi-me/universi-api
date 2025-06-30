package me.universi.user.services;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.universi.user.entities.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {
    private final JWTService jwtService;

    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Bearer ";

    public JWTFilter(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(AUTH_HEADER);
        // prefer check first if user is logged-in on session, else use JWT token to authenticate
        if(!( !jwtService.ENABLED || header == null || !header.startsWith(AUTHENTICATION_SCHEME) || LoginService.getInstance().userIsLoggedIn() )) {
            try {
                User user = jwtService.getUserFromToken(header.substring(7)); // remove "Bearer " prefix
                LoginService.getInstance().configureSessionForUser(user, null);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
