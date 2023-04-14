package me.universi.user.services;

import me.universi.api.entities.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
    Classe para manipular a falhas de login
 */

public class AutenticacaoFalhaHandler extends SimpleUrlAuthenticationFailureHandler {
    @Autowired
    private UsuarioService usuarioService;
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        if ("application/json".equals(request.getHeader("Content-Type"))) {

            Response resposta = new Response();

            resposta.success = false;
            resposta.message = usuarioService.erroSpringSecurityMemsagem(exception);

            response.setHeader("Content-Type", "application/json; charset=utf-8");
            response.getWriter().print(resposta.toString());
            response.getWriter().flush();

        } else {
            super.onAuthenticationFailure(request, response, exception);
        }
    }
}