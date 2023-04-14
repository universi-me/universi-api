package me.universi.user.controller;

import java.util.Collections;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import me.universi.api.entities.Response;
import me.universi.competencia.services.CompetenciaTipoService;
import me.universi.grupo.services.GrupoService;
import me.universi.perfil.entities.Perfil;
import me.universi.perfil.services.PerfilService;
import me.universi.user.entities.User;
import me.universi.user.enums.Autoridade;
import me.universi.user.exceptions.UsuarioException;
import me.universi.user.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

@Controller
public class UsuarioController {
    @Autowired
    public UsuarioService usuarioService;
    @Autowired
    public PerfilService perfilService;
    @Autowired
    public GrupoService grupoService;
    @Autowired
    public CompetenciaTipoService competenciaTipoService;
    @Autowired
    private Environment env;
    @Autowired
    AuthenticationManager authenticationManager;


    @PostMapping(value = "/registrar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response registrarUsuarioJson(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
        try {

            if(!Boolean.parseBoolean(env.getProperty("REGISTRAR_SE_ATIVADO"))) {
                throw new UsuarioException("Registrar-se está desativado!");
            }

            String nome = (String)body.get("username");

            // email somenta parte antes do @
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

            User user = new User();
            user.setNome(nome);
            // exclusivo para dcx
            user.setEmail(email + "@dcx.ufpb.br");
            user.setSenha(usuarioService.codificarSenha(senha));

            usuarioService.createUser(user);

            resposta.success = true;
            resposta.message = "Usuário registrado com sucesso, efetue o login para completar o cadastro.";
            return resposta;

        } catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }


    @PostMapping(value = "/conta/editar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response conta_editar(@RequestBody Map<String, Object> body, HttpSession session) {
        Response resposta = new Response();
        try {

            String password = (String)body.get("password");
            if(password == null) {
                throw new UsuarioException("Parametro password é nulo.");
            }

            String senha = (String)body.get("senha");

            User user = usuarioService.obterUsuarioNaSessao();

            // se logado com google não checkar senha
            boolean logadoComGoogle = (session.getAttribute("loginViaGoogle") != null);

            if (logadoComGoogle || usuarioService.senhaValida(user, senha)) {
                user.setSenha(usuarioService.codificarSenha(password));
                user.setCredenciais_expiradas(false);
                usuarioService.save(user);

                usuarioService.atualizarUsuarioNaSessao();

                resposta.success = true;
                resposta.message = "As Alterações foram salvas com sucesso.";

                return resposta;
            }

            resposta.message = "Credenciais Invalidas!";
            return resposta;
        }catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/admin/conta/editar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response admin_conta_editar(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
        try {

            String usuarioId = (String)body.get("usuarioId");
            if(usuarioId == null) {
                throw new UsuarioException("Parametro usuarioId é nulo.");
            }

            String username = (String)body.get("username");
            String email = (String)body.get("email");
            String senha = (String)body.get("senha");
            String nivelConta = (String)body.get("nivelConta");

            Boolean emailVerificado = (Boolean)body.get("emailVerificado");
            Boolean contaBloqueada = (Boolean)body.get("contaBloqueada");
            Boolean contaInativa = (Boolean)body.get("contaInativa");
            Boolean credenciaisExpiradas = (Boolean)body.get("credenciaisExpiradas");
            Boolean usuarioExpirado = (Boolean)body.get("usuarioExpirado");

            User userEdit = (User) usuarioService.findFirstById(Long.valueOf(usuarioId));
            if(userEdit == null) {
                throw new UsuarioException("Usuário não encontrado.");
            }

            String usernameOld = userEdit.getUsername();

            if(username != null && username.length()>0) {
                userEdit.setNome(username);
            }
            if(email != null && email.length()>0) {
                userEdit.setEmail(email);
            }
            if(senha != null && senha.length()>0) {
                userEdit.setSenha(usuarioService.codificarSenha(senha));
            }
            if(nivelConta != null && nivelConta.length()>0) {
                userEdit.setAutoridade(Autoridade.valueOf(nivelConta));
            }

            if(emailVerificado != null) {
                userEdit.setEmail_verificado(emailVerificado);
            }
            if(contaBloqueada != null) {
                userEdit.setConta_bloqueada(contaBloqueada);
            }
            if(contaInativa != null) {
                userEdit.setInativo(contaInativa);
            }
            if(credenciaisExpiradas != null) {
                userEdit.setCredenciais_expiradas(credenciaisExpiradas);
            }
            if(usuarioExpirado != null) {
                userEdit.setUsuario_expirado(usuarioExpirado);
            }

            usuarioService.save(userEdit);

            // force logout
            usuarioService.logoutUsername(usernameOld);

            resposta.success = true;
            resposta.message = "As Alterações foram salvas com sucesso, A sessão do usuário foi finalizada.";

            return resposta;

        }catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/login/google", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response conta_google(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Response resposta = new Response();
        try {

            String idTokenString = (String)body.get("token");

            if(idTokenString==null) {
                throw new UsuarioException("Parametro token é nulo.");
            }

            // verificação de segurança com o payload
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(env.getProperty("GOOGLE_CLIENT_ID")))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            HttpSession sessionReq = usuarioService.obterSessaoAtual();

            if (idToken != null) {
                Payload payload = idToken.getPayload();

                //String userId = payload.getSubject();

                String email = payload.getEmail();
                //boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");
                //String locale = (String) payload.get("locale");
                //String familyName = (String) payload.get("family_name");
                //String givenName = (String) payload.get("given_name");

                User user = null;

                try {
                    user = (User) usuarioService.findFirstByEmail(email);
                } catch (UsuarioException  e) {
                    // Registrar Usuário com conta DCX, com informações seguras do payload

                    // criar username a partir do email DCX
                    String newUsername = ((String)email.split("@")[0]).trim();
                    if(!usuarioService.usernameExiste(newUsername)) {

                        user = new User();
                        user.setNome(newUsername);
                        user.setEmail(email.trim());
                        usuarioService.createUser(user);

                        Perfil perfil = user.getPerfil();

                        if(name != null) {
                            if(name.contains(" ")) { // se tiver espaço, extrair nome e sobrenome
                                String[] nameArr = name.split(" ");
                                perfil.setNome(((String)nameArr[0]).trim());
                                perfil.setSobrenome(name.substring(nameArr[0].length()).trim());
                            } else {
                                perfil.setNome(name.trim());
                            }
                        }
                        if(pictureUrl != null) {
                            perfil.setImagem(pictureUrl.trim());
                        }

                        perfilService.save(perfil);

                        sessionReq.setAttribute("novoUsuario", true);

                    } else {
                        throw new UsuarioException("Usúario \""+newUsername+"\" já existe.");
                    }
                }

                if(user != null) {

                    if(!user.isEmail_verificado()) { // ativar selo de verificado na conta
                        user.setEmail_verificado(true);
                        usuarioService.save(user);
                    }

                    sessionReq.setAttribute("loginViaGoogle", true);

                    usuarioService.configurarSessaoParaUsuario(user, authenticationManager);

                    resposta.success = true;
                    resposta.redirectTo = usuarioService.obterUrlAoLogar();
                    resposta.message = "Usuário Logado com sucesso.";
                    return resposta;
                }

            } else {
                throw new UsuarioException("Token de Autenticação é Inválida.");
            }

            resposta.message = "Falha ao fazer login com Google.";
            return resposta;

        }catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }


}