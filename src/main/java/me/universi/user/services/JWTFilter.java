package me.universi.user.services;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.universi.api.exceptions.UniversiException;
import me.universi.user.exceptions.UserException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {

        try {
            LoginService.getInstance().configureSessionForUser( LoginService.getInstance().getUserInSession(false) );
        } catch (UniversiException e) {
            LoginService.getInstance().logout();
        }

        filterChain.doFilter(request, response);
    }
}
