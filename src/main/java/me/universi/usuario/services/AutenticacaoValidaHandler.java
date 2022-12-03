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

public class AutenticacaoValidaHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if(session != null) {

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

        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        String redirecionarParaCriarPerfil = null;
        if(usuarioService.usuarioPrecisaDePerfil(usuario)) {
            redirecionarParaCriarPerfil = "/p/" + usuario.getUsername() + "/editar";
        }


        if ("application/json".equals(request.getHeader("Content-Type"))) {

            Resposta resposta = new Resposta();
            resposta.sucess = true;
            resposta.mensagem = "Usuário Logado com sucesso.";

            SavedRequest lastRequestSaved = (SavedRequest)session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
            resposta.enderecoParaRedirecionar = lastRequestSaved!=null?lastRequestSaved.getRedirectUrl():redirecionarParaCriarPerfil!=null?redirecionarParaCriarPerfil:"/";

            response.setHeader("Content-Type", "application/json; charset=utf-8");
            response.getWriter().print(resposta.toString());
            response.getWriter().flush();

        } else {
            if(redirecionarParaCriarPerfil != null) {
                RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
                redirectStrategy.sendRedirect(request, response, redirecionarParaCriarPerfil);
            } else {
                super.onAuthenticationSuccess(request, response, authentication);
            }
        }
    }
}
