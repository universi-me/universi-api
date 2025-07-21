package me.universi.user.services;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.universi.api.entities.Response;
import me.universi.user.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    public CustomAccessDeniedHandler() {
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        Response responseBuild = Response.buildResponse(r -> {
            r.status = 403;
            throw new UserException("Área Restrita.");
        });

        response.setHeader("Content-Type", "application/json; charset=utf-8");
        response.getWriter().print(responseBuild.toString());
        response.getWriter().flush();
    }
}
