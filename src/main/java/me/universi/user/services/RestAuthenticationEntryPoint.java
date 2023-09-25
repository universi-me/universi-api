package me.universi.user.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.universi.api.entities.Response;
import me.universi.user.exceptions.UserException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

public final class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException authException) throws IOException {

        Response responseBuild = Response.buildResponse(r -> {
            r.status = 401;
            r.redirectTo = "/login";
            throw new UserException("Usuário não esta logado.");
        });

        response.setHeader("Content-Type", "application/json; charset=utf-8");
        response.getWriter().print(responseBuild.toString());
        response.getWriter().flush();

        //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }

}
