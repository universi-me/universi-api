package me.universi.usuario.controller;

import java.util.Collections;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import me.universi.api.entities.Resposta;
import me.universi.usuario.entities.Usuario;
import me.universi.usuario.exceptions.UsuarioException;
import me.universi.usuario.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

@Controller
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private Environment env;

    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpSession session) {
        session.setAttribute("error", getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION"));
        return "usuario/login";
    }

    @GetMapping("/registrar")
    public String registrar(HttpSession session) {
        return "usuario/registrar";
    }

    @ResponseBody
    @PostMapping(value = "/registrar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object registrarUsuarioJson(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            String nome = (String)body.get("username");
            String email = (String)body.get("email");
            String senha = (String)body.get("password");

            if (nome==null || nome.length()==0 || !usuarioService.usuarioRegex(nome)) {
                throw new UsuarioException("Verifique o campo Usuário!");
            }
            if (email==null || email.length()==0 || !usuarioService.emailRegex(email + "@dcx.ufpb.br")) {
                throw new UsuarioException("Verifique o campo Email!");
            }
            if (senha==null || senha.length()==0) {
                throw new UsuarioException("Verifique o campo Senha!");
            }

            if(usuarioService.usernameExiste(nome)) {
                throw new UsuarioException("Usuário \""+nome+"\" já esta cadastrado!");
            }
            if(usuarioService.emailExiste(email)) {
                throw new UsuarioException("Email \""+email+"\" já esta cadastrado!");
            }

            Usuario user = new Usuario();
            user.setNome(nome);
            user.setEmail(email + "@dcx.ufpb.br");
            user.setSenha(usuarioService.codificarSenha(senha));

            usuarioService.createUser(user);

            resposta.sucess = true;
            resposta.mensagem = "Usuário registrado com sucesso, efetue o login para completar o cadastro.";
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }
	
	@GetMapping("/conta")
    public String conta() {
        return "usuario/conta";
    }

    @ResponseBody
    @PostMapping(value = "/conta/editar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object conta_editar(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            String password = (String)body.get("password");
            if(password == null) {
                throw new UsuarioException("Parametro password é nulo.");
            }

            String senha = (String)body.get("senha");

            Usuario usuario = (Usuario) session.getAttribute("usuario");

            // se logado com google não checkar senha
            boolean logadoComGoogle = (session.getAttribute("loginViaGoogle") != null);

            if (logadoComGoogle || usuarioService.senhaValida(usuario, senha)) {
                usuario.setSenha(usuarioService.codificarSenha(password));
                usuarioService.save(usuario);

                usuarioService.atualizarUsuarioNaSessao(session);

                resposta.sucess = true;
                resposta.mensagem = "As Alterações foram salvas com sucesso.";

                return resposta;
            }

            resposta.mensagem = "Credenciais Invalidas!";
            return resposta;
        }catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @ResponseBody
    @PostMapping(value = "/login/google", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object conta_google(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            String idTokenString = (String)body.get("token");

            if(idTokenString==null) {
                throw new UsuarioException("Parametro token é nulo.");
            }

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(env.getProperty("GOOGLE_CLIENT_ID")))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                Payload payload = idToken.getPayload();

                //String userId = payload.getSubject();

                String email = payload.getEmail();
                //boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
                //String name = (String) payload.get("name");
                //String pictureUrl = (String) payload.get("picture");
                //String locale = (String) payload.get("locale");
                //String familyName = (String) payload.get("family_name");
                //String givenName = (String) payload.get("given_name");



                Usuario usuario = (Usuario)usuarioService.findFirstByEmail(email);

                if(usuario != null) {

                    HttpSession sessionReq = request.getSession(true);

                    Authentication authentication = new UsernamePasswordAuthenticationToken(usuario, null, AuthorityUtils.createAuthorityList(usuario.getAutoridade().name()));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    if(!usuario.isEmail_verificado()) {
                        usuario.setEmail_verificado(true);
                        usuarioService.save(usuario);
                    }

                    sessionReq.setAttribute("loginViaGoogle", true);

                    usuarioService.configurarSessaoParaUsuario(sessionReq, usuario);

                    String redirecionarParaCriarPerfil = null;
                    if(usuario.getPerfil()==null || usuario.getPerfil().getNome()==null) {
                        redirecionarParaCriarPerfil = "/p/" + usuario.getUsername() + "/editar";
                    }

                    resposta.sucess = true;
                    resposta.enderecoParaRedirecionar = redirecionarParaCriarPerfil==null?"/conta":redirecionarParaCriarPerfil;
                    resposta.mensagem = "Usuário Logado com sucesso.";
                    return resposta;
                }

            } else {
                throw new UsuarioException("Token de Autenticação é Inválida.");
            }

            resposta.mensagem = "Falha ao fazer login com Google.";
            return resposta;

        }catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
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