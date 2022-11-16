package me.universi.usuario.controller;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import me.universi.grupo.enums.GrupoTipo;
import me.universi.usuario.entities.Usuario;
import me.universi.usuario.services.SecurityUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UsuarioController {
    @Autowired
    private SecurityUserDetailsService usuarioService;

    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpSession session) {
        session.setAttribute("error", getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION"));
        return "usuario/login";
    }

    @GetMapping("/registrar")
    public String registrar(HttpSession session) {
        return "usuario/registrar";
    }

    @RequestMapping(value = "/registrar", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String registrarUsuarioForm(@RequestParam Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        return registrarUsuario(body, request, session);
    }

    @RequestMapping(value = "/registrar", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String registrarUsuarioJson(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        return registrarUsuario(body, request, session);
    }

    public String registrarUsuario(Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        try {

            String nome = (String)body.get("username");
            String email = (String)body.get("email");
            String senha = (String)body.get("password");

            if (nome==null || nome.length()==0 || !usuarioService.usuarioRegex(nome)) {
                throw new Exception("Verifique o campo Usuário!");
            }
            if (email==null || email.length()==0 || !usuarioService.emailRegex(email + "@dcx.ufpb.br")) {
                throw new Exception("Verifique o campo Email!");
            }
            if (senha==null || senha.length()==0) {
                throw new Exception("Verifique o campo Senha!");
            }

            if(usuarioService.usernameExiste(nome)) {
                throw new Exception("Usuário \""+nome+"\" já esta cadastrado!");
            }
            if(usuarioService.emailExiste(email)) {
                throw new Exception("Email \""+email+"\" já esta cadastrado!");
            }

            Usuario user = new Usuario();
            user.setNome(nome);
            user.setEmail(email + "@dcx.ufpb.br");
            user.setSenha(usuarioService.codificarSenha(senha));

            usuarioService.createUser(user);
        } catch (Exception e) {
            session.setAttribute("error", e.getMessage());
            return "redirect:/registrar?error";
        }
        session.setAttribute("registrado", "Usuário registrado com sucesso, efetue o login.");
        return "redirect:/login?registrado";
    }
	
	@GetMapping("/conta")
    public String conta() {
        return "usuario/conta";
    }

    @RequestMapping("/conta/editar")
    public Object conta_editar(HttpSession session, @RequestParam("password") String password, @RequestParam("senha") String senha) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            if (usuarioService.senhaValida(usuario, senha)) {
                usuario.setSenha(usuarioService.codificarSenha(password));
                usuarioService.save(usuario);
                session.setAttribute("salvo", "As Alterações foram salvas com sucesso.");
                return "redirect:/conta?salvo";
            }
            session.setAttribute("error", "Credenciais Invalidas!");
            return "redirect:/conta?error";
        }catch (Exception e) {
            session.setAttribute("error", e.getMessage());
            return "redirect:/conta?error";
        }
    }

    private String getErrorMessage(HttpServletRequest request, String key) {
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