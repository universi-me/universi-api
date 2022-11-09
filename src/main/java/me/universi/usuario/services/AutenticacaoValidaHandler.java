package me.universi.usuario.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;

public class AutenticacaoValidaHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    private SecurityUserDetailsService userDetailsManager;
    @Autowired HttpSession session;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        String username = "";
        if(authentication.getPrincipal() instanceof Principal) {
            username = ((Principal)authentication.getPrincipal()).getName();
        }else {
            username = ((UserDetails)authentication.getPrincipal()).getUsername();
        }
        // Salvar usuario na sessao
        UserDetails usuario = userDetailsManager.loadUserByUsername(username);
        session.setAttribute("usuario", usuario);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
