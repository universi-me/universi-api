package me.universi.usuario.controller;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import me.universi.usuario.entities.Usuario;
import me.universi.usuario.services.SecurityUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UsuarioController
{
    @Autowired
    private SecurityUserDetailsService usuarioService;

    @GetMapping("/conta")
    public String conta() {
        return "usuario/conta";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpSession session)
    {
        session.setAttribute("error", getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION"));
        return "usuario/login";
    }

    @GetMapping("/registrar")
    public String registrar(HttpSession session)
    {
        return "usuario/registrar";
    }

    @RequestMapping(value = "/registrar", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String registrarUsuarioForm(@RequestParam Map<String, Object> body, HttpServletRequest request, HttpSession session)
    {
        return registrarUsuario(body, request, session);
    }

    @RequestMapping(value = "/registrar", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String registrarUsuarioJson(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session)
    {
        return registrarUsuario(body, request, session);
    }

    public String registrarUsuario(Map<String, Object> body, HttpServletRequest request, HttpSession session)
    {
        try {
            Usuario user = new Usuario();

            user.setNome((String)body.get("username"));
            user.setEmail((String)body.get("email"));
            user.setSenha(usuarioService.codificarSenha((String)body.get("password")));

            if (user.getNome()==null || user.getNome().length()==0) {
                throw new Exception("Verifique o campo Usuário!");
            }
            if (user.getNome().contains(" ")) {
                throw new Exception("O campo Usuário não pode conter espaços!");
            }
            if (user.getEmail()==null || user.getEmail().length()==0 || !user.getEmail().contains("@")) {
                throw new Exception("Verifique o campo Email!");
            }
            if (user.getSenha()==null || user.getSenha().length()==0) {
                throw new Exception("Verifique o campo Senha!");
            }
            if(usuarioService.usernameExiste(user.getUsername())) {
                throw new Exception("Usuário \""+user.getUsername()+"\" já esta cadastrado!");
            }

            usuarioService.createUser(user);
        } catch (Exception e) {
            session.setAttribute("error", e.getMessage());
            return "redirect:/registrar?error";
        }
        session.setAttribute("registrado", "Usuário registrado com sucesso, efetue o login.");
        return "redirect:/login?registrado";
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