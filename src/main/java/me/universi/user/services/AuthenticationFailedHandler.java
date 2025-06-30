package me.universi.user.services;

import me.universi.api.entities.Response;
import me.universi.user.exceptions.UserException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;

/*
    Classe para manipular a falhas de login
 */
@Component
public class AuthenticationFailedHandler extends SimpleUrlAuthenticationFailureHandler {

    public AuthenticationFailedHandler() {

    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        if ("application/json".equals(request.getHeader("Content-Type"))) {

            Response resp = Response.buildResponse(r -> {
                r.status = 401;
                r.alertOptions.put("icon", "warning");
                r.alertOptions.put("title", "Falha na autenticação");
                throw new UserException(LoginService.getInstance().getLastSpringSecurityError(exception));
            });

            response.setHeader("Content-Type", "application/json; charset=utf-8");
            response.getWriter().print(resp.toString());
            response.getWriter().flush();

        } else {
            super.onAuthenticationFailure(request, response, exception);
        }
    }
}
