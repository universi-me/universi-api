package me.universi.user.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import me.universi.api.entities.Response;
import me.universi.user.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.util.Map;

/*
    Classe para compatibilidade de login via JSON
 */

public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private String jsonUsername;
    private String jsonPassword;

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        String password = null;

        if ("application/json".equals(request.getHeader("Content-Type"))) {
            password = this.jsonPassword;
        }else{
            password = super.obtainPassword(request);
        }

        return password;
    }

    @Override
    protected String obtainUsername(HttpServletRequest request){
        String username = null;

        if ("application/json".equals(request.getHeader("Content-Type"))) {
            username = this.jsonUsername;
        }else{
            username = super.obtainUsername(request);
        }

        return username;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response){
        if (request.getMethod().equals("POST") && "application/json".equals(request.getHeader("Content-Type"))) {
            try {

                StringBuffer sb = new StringBuffer();
                String line = null;

                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null){
                    sb.append(line);
                }

                ObjectMapper mapper = new ObjectMapper();
                Map mapRequest = mapper.readValue(sb.toString(), Map.class);

                this.jsonUsername = (String)( mapRequest.containsKey("username")? mapRequest.get("username") : mapRequest.get("email"));
                this.jsonPassword = (String)mapRequest.get("password");

                UserService.getInstance().checkRecaptchaWithToken(mapRequest.get("recaptchaToken"));

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
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }
}
