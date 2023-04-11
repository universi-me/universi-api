package me.universi.usuario.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import me.universi.api.entities.Resposta;
import me.universi.competencia.services.CompetenciaTipoService;
import me.universi.grupo.enums.GrupoTipo;
import me.universi.grupo.services.GrupoService;
import me.universi.perfil.entities.Perfil;
import me.universi.perfil.services.PerfilService;
import me.universi.usuario.entities.Usuario;
import me.universi.usuario.enums.Autoridade;
import me.universi.usuario.exceptions.UsuarioException;
import me.universi.usuario.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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

    @GetMapping("/login")
    public String login() {
        if(usuarioService.usuarioEstaLogado()) {
            return "redirect:"+usuarioService.obterUrlAoLogar();
        }
        return "usuario/login";
    }

    @GetMapping("/entrar")
    public String entrar() {
        return "redirect:"+usuarioService.obterUrlAoLogar();
    }

    @GetMapping("/registrar")
    public String registrar() {
        if(usuarioService.usuarioEstaLogado()) {
            return "redirect:"+usuarioService.obterUrlAoLogar();
        }
        return "usuario/registrar";
    }

    @GetMapping("/admin/**")
    public String admin_handle(HttpServletRequest request, ModelMap map) {
        try {

            // obter diretorio caminho url
            String requestPathSt = request.getRequestURI();

            map.addAttribute("grupoService", grupoService);
            map.addAttribute("usuarioService", usuarioService);
            map.addAttribute("competenciaTipoService", competenciaTipoService);

            if(requestPathSt.startsWith("/admin/conta")) {
                boolean flagEditar = requestPathSt.endsWith("/editar");
                String[] componentesArr = requestPathSt.split("/");
                if (flagEditar) {
                    // remover ultimo componente no caminho, flags
                    componentesArr = Arrays.copyOf(componentesArr, componentesArr.length - 1);
                    map.addAttribute("tiposAutoridades", Autoridade.values());

                    String usuario = componentesArr[componentesArr.length - 1];
                    Usuario userGet = (Usuario) usuarioService.loadUserByUsername(usuario);
                    map.put("usuario", userGet);
                }

            }

            if(requestPathSt.startsWith("/admin/grupo")) {
                map.addAttribute("tiposGrupo", GrupoTipo.values());
            }

        } catch (Exception e) {
            map.put("error", "Admin: " + e.getMessage());
        }
        return "usuario/admin/admin_index";
    }

    @PostMapping(value = "/registrar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Resposta registrarUsuarioJson(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
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

            Usuario user = new Usuario();
            user.setNome(nome);
            // exclusivo para dcx
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

    @PostMapping(value = "/conta/editar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Resposta conta_editar(@RequestBody Map<String, Object> body, HttpSession session) {
        Resposta resposta = new Resposta();
        try {

            String password = (String)body.get("password");
            if(password == null) {
                throw new UsuarioException("Parametro password é nulo.");
            }

            String senha = (String)body.get("senha");

            Usuario usuario = usuarioService.obterUsuarioNaSessao();

            // se logado com google não checkar senha
            boolean logadoComGoogle = (session.getAttribute("loginViaGoogle") != null);

            if (logadoComGoogle || usuarioService.senhaValida(usuario, senha)) {
                usuario.setSenha(usuarioService.codificarSenha(password));
                usuario.setCredenciais_expiradas(false);
                usuarioService.save(usuario);

                usuarioService.atualizarUsuarioNaSessao();

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

    @PostMapping(value = "/admin/conta/editar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Resposta admin_conta_editar(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
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

            Usuario userEdit = (Usuario) usuarioService.findFirstById(Long.valueOf(usuarioId));
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

            resposta.sucess = true;
            resposta.mensagem = "As Alterações foram salvas com sucesso, A sessão do usuário foi finalizada.";

            return resposta;

        }catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/login/google", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Resposta conta_google(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Resposta resposta = new Resposta();
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

                Usuario usuario = null;

                try {
                    usuario = (Usuario) usuarioService.findFirstByEmail(email);
                } catch (UsuarioException  e) {
                    // Registrar Usuário com conta DCX, com informações seguras do payload

                    // criar username a partir do email DCX
                    String newUsername = ((String)email.split("@")[0]).trim();
                    if(!usuarioService.usernameExiste(newUsername)) {

                        usuario = new Usuario();
                        usuario.setNome(newUsername);
                        usuario.setEmail(email.trim());
                        usuarioService.createUser(usuario);

                        Perfil perfil = usuario.getPerfil();

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

                if(usuario != null) {
                    
                    // Forçar o login já que q usuário entrou com a conta google.
                    PreAuthenticatedAuthenticationToken preAuthenticatedAuthenticationToken = new PreAuthenticatedAuthenticationToken(usuario, usuario.getUsername(), AuthorityUtils.createAuthorityList(usuario.getAutoridade().name()));
                    preAuthenticatedAuthenticationToken.setDetails(new WebAuthenticationDetails(request));
                    preAuthenticatedAuthenticationToken.setAuthenticated(false);

                    Authentication authentication = authenticationManager.authenticate(preAuthenticatedAuthenticationToken);

                    SecurityContext securityContext = SecurityContextHolder.getContext();
                    securityContext.setAuthentication(authentication);
                    sessionReq.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

                    if(!usuario.isEmail_verificado()) { // ativar selo de verificado na conta
                        usuario.setEmail_verificado(true);
                        usuarioService.save(usuario);
                    }

                    sessionReq.setAttribute("loginViaGoogle", true);

                    usuarioService.configurarSessaoParaUsuario(usuario);

                    resposta.sucess = true;
                    resposta.enderecoParaRedirecionar = usuarioService.obterUrlAoLogar();
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


}