package me.universi.usuario.services;

import me.universi.usuario.entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        String username = "";
        if(authentication.getPrincipal() instanceof Principal) {
            username = ((Principal)authentication.getPrincipal()).getName();
        }else {
            username = ((UserDetails)authentication.getPrincipal()).getUsername();
        }
        Usuario usuario = (Usuario)userDetailsManager.loadUserByUsername(username);

        // Salvar usuario na sessao
        session.setAttribute("usuario", usuario);

        // Set session inatividade do usuario em 10min
        session.setMaxInactiveInterval(10*60);

        // usuário não tem perfil, redirecionar para editar perfil
        //if(usuario.getPerfil()==null) {
        //    RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        //    redirectStrategy.sendRedirect(request, response, "/p/" + usuario.getUsername() + "/editar");
        //} else {
        //    super.onAuthenticationSuccess(request, response, authentication);
        //}

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
