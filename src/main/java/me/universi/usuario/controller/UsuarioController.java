package me.universi.usuario.controller;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import me.universi.usuario.entities.Usuario;
import me.universi.usuario.services.SecurityUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UsuarioController
{
    @Autowired private SecurityUserDetailsService userDetailsManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/conta")
    public String conta() {
        return "conta";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpSession session)
    {
        session.setAttribute("error", getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION"));
        return "login";
    }

    @GetMapping("/registrar")
    public String registrar(HttpSession session)
    {
        return "registrar";
    }

    @PostMapping(value = "/registrar", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String registrarUsuarioForm(@RequestParam Map<String, Object> body, HttpServletRequest request, HttpSession session)
    {
        return registrarUsuario(body, request, session);
    }

    @PostMapping(value = "/registrar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String registrarUsuarioJson(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session)
    {
        return registrarUsuario(body, request, session);
    }

    public String registrarUsuario(Map<String, Object> body, HttpServletRequest request, HttpSession session)
    {
        try {
            Usuario user = new Usuario();
            user.setEmail((String)body.get("username"));
            user.setSenha(passwordEncoder.encode((String)body.get("password")));
            user.setNome((String)body.get("name"));
            userDetailsManager.createUser(user);
        } catch (Exception e) {
            session.setAttribute("error", e.getMessage());
            return "redirect:/registrar?error";
        }
        return "redirect:/login";
    }

    private String getErrorMessage(HttpServletRequest request, String key)
    {
        Exception exception = (Exception) request.getSession().getAttribute(key);
        String error = null;
        if (exception instanceof BadCredentialsException) {
            error = "Credenciais Invalidas!";
        } else if(exception != null) {
            error = exception.getMessage();
        }
        return error;
    }
}