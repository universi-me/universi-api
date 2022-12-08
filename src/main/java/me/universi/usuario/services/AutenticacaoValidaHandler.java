package me.universi.usuario.services;

import me.universi.api.entities.Resposta;
import me.universi.usuario.entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;

/*
    Classe para manipular quando o usuario efetuar o login
 */
public class AutenticacaoValidaHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        HttpSession session = request.getSession(true);

        String username = null;
        if (authentication.getPrincipal() instanceof Principal) {
            username = ((Principal) authentication.getPrincipal()).getName();
        } else {
            username = ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        if(username != null) {
            Usuario usuario = (Usuario) usuarioService.loadUserByUsername(username);
            usuarioService.configurarSessaoParaUsuario(usuario);
        }

        if ("application/json".equals(request.getHeader("Content-Type"))) { // request foi via JSON

            Resposta resposta = new Resposta();
            resposta.sucess = true;
            resposta.mensagem = "Usuário Logado com sucesso.";

            resposta.enderecoParaRedirecionar = usuarioService.obterUrlAoLogar();

            response.setHeader("Content-Type", "application/json; charset=utf-8");
            response.getWriter().print(resposta.toString());
            response.getWriter().flush();

        } else {

            RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
            redirectStrategy.sendRedirect(request, response, usuarioService.obterUrlAoLogar());
            //super.onAuthenticationSuccess(request, response, authentication);

        }
    }
}
