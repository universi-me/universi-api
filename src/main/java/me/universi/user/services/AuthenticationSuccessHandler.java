package me.universi.user.services;

import me.universi.api.entities.Response;
import me.universi.profile.enums.Gender;
import me.universi.user.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import org.springframework.stereotype.Component;

/*
    Classe para manipular quando o usuario efetuar o login
 */
@Component
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        String username;
        User user;

        UserService userService = UserService.getInstance();

        if (authentication.getPrincipal() instanceof Principal) {
            username = ((Principal) authentication.getPrincipal()).getName();
        } else {
            username = ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        if(username != null) {
            user = (User) userService.loadUserByUsername(username);

            if(user != null && user.isTemporarilyPassword()) {
                if(user.getRecoveryPasswordToken() == null || user.getRecoveryPasswordToken().isEmpty()) {
                    AccountService.getInstance().generateRecoveryPasswordTokenForUser(user);
                }
                Response responseBuild = Response.buildResponse(r -> {
                    r.status = 201;
                    r.redirectTo = "/recovery-password/" + user.getRecoveryPasswordToken();
                });
                response.setHeader("Content-Type", "application/json; charset=utf-8");
                response.getWriter().print(responseBuild.toString());
                response.getWriter().flush();
                response.getWriter().close();
                return;
            }

            LoginService.getInstance().configureSessionForUser(user);
        } else {
            user = null;
        }

        if ("application/json".equals(request.getHeader("Content-Type"))) { // request via JSON

            Response responseBuild = Response.buildResponse(r -> {
                if(!userService.userNeedAnProfile(user, false) && user.getProfile().getGender() != Gender.O)
                    r.message = (user.getProfile().getGender() == Gender.F ? "Bem-vinda, " : "Bem-vindo, ")+user.getProfile().getFirstname()+".";
                else if (!userService.userNeedAnProfile(user, false) && user.getProfile().getGender() == Gender.O)
                    r.message = "Boas vindas, "+user.getProfile().getFirstname()+".";
                else
                    r.message = "Boas vindas, "+user.getName()+".";
                r.redirectTo = LoginService.getInstance().getUrlWhenLogin();
                r.token = JWTService.getInstance().buildTokenForUser(user);
                r.body.put("user", user);
            });

            response.setHeader("Content-Type", "application/json; charset=utf-8");
            response.getWriter().print(responseBuild.toString());
            response.getWriter().flush();
            response.getWriter().close();
        } else {

            RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
            redirectStrategy.sendRedirect(request, response, LoginService.getInstance().getUrlWhenLogin());
            //super.onAuthenticationSuccess(request, response, authentication);

        }
    }
}
