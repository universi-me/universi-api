package me.universi.user.services;

import me.universi.api.entities.Response;
import me.universi.profile.enums.Gender;
import me.universi.user.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;

/*
    Classe para manipular quando o usuario efetuar o login
 */
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    @Autowired
    public AuthenticationSuccessHandler(UserService userService, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        String username;
        User user;

        if (authentication.getPrincipal() instanceof Principal) {
            username = ((Principal) authentication.getPrincipal()).getName();
        } else {
            username = ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        if(username != null) {
            user = (User) userService.loadUserByUsername(username);
            userService.configureSessionForUser(user, authenticationManager);
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
                r.redirectTo = userService.getUrlWhenLogin();
                r.token = jwtService.buildTokenForUser(user);
                r.body.put("user", user);
            });

            response.setHeader("Content-Type", "application/json; charset=utf-8");
            response.getWriter().print(responseBuild.toString());
            response.getWriter().flush();

        } else {

            RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
            redirectStrategy.sendRedirect(request, response, userService.getUrlWhenLogin());
            //super.onAuthenticationSuccess(request, response, authentication);

        }
    }
}
