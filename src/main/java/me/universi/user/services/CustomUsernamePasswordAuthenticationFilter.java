package me.universi.user.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.universi.api.entities.Response;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

/*
    Classe para compatibilidade de login via JSON
 */

public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private String jsonUsername;
    private String jsonPassword;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected String obtainPassword(HttpServletRequest request) {

        if ("application/json".equals(request.getHeader("Content-Type"))) {
            return this.jsonPassword;
        }

        return super.obtainPassword(request);
    }

    @Override
    protected String obtainUsername(HttpServletRequest request){

        if ("application/json".equals(request.getHeader("Content-Type"))) {
            return this.jsonUsername;
        }

        return super.obtainUsername(request);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response){
        if (request.getMethod().equals("POST") && "application/json".equals(request.getHeader("Content-Type"))) {
            try {

                Map mapRequest = objectMapper.readValue(request.getInputStream().readAllBytes(), Map.class);

                this.jsonUsername = (String)( mapRequest.containsKey("username")? mapRequest.get("username") : mapRequest.get("email"));
                this.jsonPassword = (String)mapRequest.get("password");

                GoogleService.getInstance().checkRecaptchaWithTokenTransactional(mapRequest.get("recaptchaToken"));

            } catch (Exception e) {
                try {
                    Response responseBuild = Response.buildResponse(r -> {
                        r.status = 401;
                        r.alertOptions.put("icon", "warning");
                        r.alertOptions.put("title", "Falha na autenticação");
                        throw e;
                    });
                    response.setHeader("Content-Type", "application/json; charset=utf-8");
                    response.getWriter().print(responseBuild.toString());
                    response.getWriter().flush();
                } catch (Exception ignored) {
                }
                return null;
            }
        }

        return super.attemptAuthentication(request, response);
    }

    @Override
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }
}
