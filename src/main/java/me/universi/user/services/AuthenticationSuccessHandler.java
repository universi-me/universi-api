package me.universi.user.services;

import me.universi.api.entities.Response;
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
    @Autowired
    private UserService userService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private JWTService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        HttpSession session = request.getSession(true);

        String username = null;
        User user = null;

        if (authentication.getPrincipal() instanceof Principal) {
            username = ((Principal) authentication.getPrincipal()).getName();
        } else {
            username = ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        if(username != null) {
            user = (User) userService.loadUserByUsername(username);
            userService.configurarSessaoParaUsuario(user, authenticationManager);
        }

        if ("application/json".equals(request.getHeader("Content-Type"))) { // request via JSON

            Response responseBuild = new Response();
            responseBuild.success = true;
            responseBuild.message = "Usuário Logado com sucesso.";

            responseBuild.redirectTo = userService.obterUrlAoLogar();

            responseBuild.token = jwtService.buildTokenForUser(user);

            responseBuild.body.put("user", user);

            response.setHeader("Content-Type", "application/json; charset=utf-8");
            response.getWriter().print(responseBuild.toString());
            response.getWriter().flush();

        } else {

            RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
            redirectStrategy.sendRedirect(request, response, userService.obterUrlAoLogar());
            //super.onAuthenticationSuccess(request, response, authentication);

        }
    }
}
