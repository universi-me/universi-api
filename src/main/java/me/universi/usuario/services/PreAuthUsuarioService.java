package me.universi.usuario.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

@Service
public class PreAuthUsuarioService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private SessionRegistry sessionRegistry;

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {

        String username = ((UserDetails)token.getPrincipal()).getUsername();
        UserDetails user = usuarioService.loadUserByUsername(username);

        if(user != null) {
            String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
            sessionRegistry.registerNewSession(sessionId, user);
        }

        return user;
    }
}
