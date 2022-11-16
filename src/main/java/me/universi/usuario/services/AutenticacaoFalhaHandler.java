package me.universi.usuario.services;

import me.universi.api.entities.Resposta;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AutenticacaoFalhaHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        if ("application/json".equals(request.getHeader("Content-Type"))) {

            Resposta resposta = new Resposta();
            resposta.sucess = false;
            resposta.mensagem = "Credenciais Inválidas!";

            response.setHeader("Content-Type", "application/json; charset=utf-8");
            response.getWriter().print(resposta.toString());
            response.getWriter().flush();


        } else {
            super.onAuthenticationFailure(request, response, exception);
        }
    }
}
