package me.universi.user.controller;

import java.util.Collections;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import me.universi.api.entities.Response;
import me.universi.competence.services.CompetenceTypeService;
import me.universi.group.services.GroupService;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.PerfilService;
import me.universi.user.entities.User;
import me.universi.user.enums.Authority;
import me.universi.user.exceptions.UserException;
import me.universi.user.services.JWTService;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

@RestController
@RequestMapping(value = "/api")
public class UserController {
    @Autowired
    public UserService userService;
    @Autowired
    public PerfilService perfilService;
    @Autowired
    public GroupService grupoService;
    @Autowired
    public CompetenceTypeService competenciaTipoService;
    @Autowired
    private Environment env;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private JWTService jwtService;

    @GetMapping(value = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response account() {
        Response response = new Response();
        try {

            if(userService.userIsLoggedIn()) {
                response.success = true;
                response.body.put("user", userService.getUserInSession());
                return response;
            }

            throw new UserException("Usuário não esta logado.");

        }catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @GetMapping("/login")
    @ResponseBody
    public Response login() {
        Response response = new Response();
        try {

            if(userService.userIsLoggedIn()) {
                response.success = true;
                response.message = "Usuário está logado.";
                return response;
            }

            throw new UserException("Usuário não esta logado.");

        }catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response signup(@RequestBody Map<String, Object> body) {
        Response response = new Response();
        try {

            // check if register is enabled
            if(!Boolean.parseBoolean(env.getProperty("REGISTRAR_SE_ATIVADO"))) {
                throw new UserException("Registrar-se está desativado!");
            }

            String username = (String)body.get("username");

            // email only part before @
            String email = (String)body.get("email");

            String password = (String)body.get("password");

            if (username==null || username.length()==0 || !userService.usernameRegex(username)) {
                throw new UserException("Verifique o campo Usuário!");
            }
            if (email==null || email.length()==0 || !userService.emailRegex(email + "@dcx.ufpb.br")) {
                throw new UserException("Verifique o campo Email!");
            }
            if (password==null || password.length()==0) {
                throw new UserException("Verifique o campo Senha!");
            }

            if(userService.usernameExist(username)) {
                throw new UserException("Usuário \""+username+"\" já esta cadastrado!");
            }
            if(userService.emailExist(email)) {
                throw new UserException("Email \""+email+"\" já esta cadastrado!");
            }

            User user = new User();
            user.setName(username);
            // only for dcx
            user.setEmail(email + "@dcx.ufpb.br");
            user.setPassword(userService.encodePassword(password));

            userService.createUser(user);

            response.success = true;
            response.message = "Usuário registrado com sucesso, efetue o login para completar o cadastro.";
            return response;

        } catch (Exception e) {
            response.message = e.getMessage();
            return response;
        }
    }


    @PostMapping(value = "/account/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response account_edit(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
        try {

            String password = (String)body.get("newPassword");
            if(password == null) {
                throw new UserException("Parametro password é nulo.");
            }

            String senha = (String)body.get("password");

            User user = userService.getUserInSession();

            // if logged with google don't check password
            HttpSession session = userService.getActiveSession();
            boolean logadoComGoogle = (session.getAttribute("loginViaGoogle") != null);

            if (logadoComGoogle || userService.passwordValid(user, senha)) {
                user.setPassword(userService.encodePassword(password));
                user.setExpired_credentials(false);
                userService.save(user);

                userService.updateUserInSession();

                resposta.success = true;
                resposta.message = "As Alterações foram salvas com sucesso.";

                return resposta;
            }

            throw new UserException("Credenciais Invalidas!");

        }catch (Exception e) {
            resposta.message = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/admin/account/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response admin_account_edit(@RequestBody Map<String, Object> body) {
        Response resposta = new Response();
        try {

            String userId = (String)body.get("userId");
            if(userId == null) {
                throw new UserException("Parametro userId é nulo.");
            }

            String username = (String)body.get("username");
            String email = (String)body.get("email");
            String password = (String)body.get("password");
            String authorityLevel = (String)body.get("authorityLevel");

            Boolean emailVerified = (Boolean)body.get("emailVerified");
            Boolean blockedAccount = (Boolean)body.get("blockedAccount");
            Boolean inactiveAccount = (Boolean)body.get("inactiveAccount");
            Boolean credentialsExpired = (Boolean)body.get("credentialsExpired");
            Boolean expiredUser = (Boolean)body.get("expiredUser");

            User userEdit = (User) userService.findFirstById(Long.valueOf(userId));
            if(userEdit == null) {
                throw new UserException("Usuário não encontrado.");
            }

            String usernameOld = userEdit.getUsername();

            if(username != null && username.length()>0) {
                if(userService.usernameRegex(username)) {
                    userEdit.setName(username);
                } else {
                    throw new UserException("username está com formato inválido!");
                }
            }
            if(email != null && email.length()>0) {
                userEdit.setEmail(email);
            }
            if(password != null && password.length()>0) {
                userEdit.setPassword(userService.encodePassword(password));
            }
            if(authorityLevel != null && authorityLevel.length()>0) {
                userEdit.setAuthority(Authority.valueOf(authorityLevel));
            }

            if(emailVerified != null) {
                userEdit.setEmail_verified(emailVerified);
            }
            if(blockedAccount != null) {
                userEdit.setBlocked_account(blockedAccount);
            }
            if(inactiveAccount != null) {
                userEdit.setInactive(inactiveAccount);
            }
            if(credentialsExpired != null) {
                userEdit.setExpired_credentials(credentialsExpired);
            }
            if(expiredUser != null) {
                userEdit.setExpired_user(expiredUser);
            }

            userService.save(userEdit);

            // force logout
            userService.logoutUsername(usernameOld);

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
    public Response login_google(@RequestBody Map<String, Object> body) {
        Response responseBuild = new Response();
        try {

            String idTokenString = (String)body.get("token");

            if(idTokenString==null) {
                throw new UserException("Parametro token é nulo.");
            }

            // check if payload is valid
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(env.getProperty("GOOGLE_CLIENT_ID")))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            HttpSession sessionReq = userService.getActiveSession();

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
                    user = (User) userService.findFirstByEmail(email);
                } catch (UserException e) {
                    // register user with DCX account, with secure payload information

                    // create username from email DCX
                    String newUsername = ((String)email.split("@")[0]).trim();
                    if(!userService.usernameExist(newUsername)) {

                        user = new User();
                        user.setName(newUsername);
                        user.setEmail(email.trim());
                        userService.createUser(user);

                        Profile profile = user.getProfile();

                        if(name != null) {
                            // if have space, extract first name and last name
                            if(name.contains(" ")) {
                                String[] nameArr = name.split(" ");
                                profile.setFirstname(((String)nameArr[0]).trim());
                                profile.setLastname(name.substring(nameArr[0].length()).trim());
                            } else {
                                profile.setFirstname(name.trim());
                            }
                        }
                        if(pictureUrl != null) {
                            profile.setImage(pictureUrl.trim());
                        }

                        perfilService.save(profile);

                        sessionReq.setAttribute("novoUsuario", true);

                    } else {
                        throw new UserException("Usúario \""+newUsername+"\" já existe.");
                    }
                }

                if(user != null) {
                    
                    // enable verified seal on account
                    if(!user.isEmail_verified()) {
                        user.setEmail_verified(true);
                        userService.save(user);
                    }

                    sessionReq.setAttribute("loginViaGoogle", true);

                    userService.configureSessionForUser(user, authenticationManager);

                    responseBuild.success = true;
                    responseBuild.redirectTo = userService.getUrlWhenLogin();
                    responseBuild.message = "Usuário Logado com sucesso.";

                    responseBuild.token = jwtService.buildTokenForUser(user);

                    responseBuild.body.put("user", user);

                    return responseBuild;
                }

            } else {
                throw new UserException("Token de Autenticação é Inválida.");
            }

            throw new UserException("Falha ao fazer login com Google.");

        }catch (Exception e) {
            responseBuild.message = e.getMessage();
            return responseBuild;
        }
    }


}