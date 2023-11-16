package me.universi.user.controller;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import me.universi.api.entities.Response;
import me.universi.group.services.GroupService;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.user.entities.User;
import me.universi.user.enums.Authority;
import me.universi.user.exceptions.UserException;
import me.universi.user.services.JWTService;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

@RestController
@RequestMapping(value = "/api")
public class UserController {
    private final UserService userService;
    private final ProfileService profileService;
    private final GroupService groupService;
    private final Environment environment;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    @Autowired
    public UserController(UserService userService, ProfileService profileService, Environment environment, AuthenticationManager authenticationManager, JWTService jwtService, GroupService groupService) {
        this.userService = userService;
        this.profileService = profileService;
        this.environment = environment;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.groupService = groupService;
    }

    @GetMapping(value = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response account() {
        return Response.buildResponse(response -> {

            if(userService.userIsLoggedIn()) {
                response.success = true;
                response.body.put("user", userService.getUserInSession());
            } else {
                response.success = false;
                response.status = 401;
            }

            HttpSession session = userService.getActiveSession();
            if(userService.getInSession("account_confirmed") != null) {

                response.success = true;
                response.redirectTo = "/login";

                response.message = userService.getInSession("message_account_confirmed").toString();

                response.alertOptions.put("title", "Confirmação de Conta");
                response.alertOptions.put("icon", "success");
                response.alertOptions.put("modalAlert", true);
                response.alertOptions.put("timer", null);

                userService.removeInSession("account_confirmed");
                userService.removeInSession("message_account_confirmed");
            }

        });
    }

    @GetMapping("/logout")
    @ResponseBody
    public Response logout() {
        return Response.buildResponse(response -> {

            if(userService.userIsLoggedIn()) {
                userService.logout();
                response.success = true;
                response.message = "A sua sessão foi finalizada com sucesso.";
                response.redirectTo = userService.getUrlWhenLogout();
                return;
            }

            throw new UserException("Usuário não esta logado.");

        });
    }

    @PostMapping(value = "/username-available", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response available_username_check(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            String username = (String)body.get("username");
            response.success = userService.usernameRegex(username) && !userService.usernameExist(username);
        });
    }

    @PostMapping(value = "/email-available", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response available_email_check(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            String email = (String)body.get("email");
            response.success = userService.emailRegex(email) && !userService.emailExist(email) && GroupService.getInstance().emailAvailableForOrganization(email);
        });
    }

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response signup(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            response.alertOptions.put("title", "Registro de Conta");

            // check if register is enabled
            if(!Boolean.parseBoolean(environment.getProperty("SIGNUP_ENABLED"))) {
                throw new UserException("Registrar-se está desativado!");
            }

            String username = (String)body.get("username");
            String email = (String)body.get("email");

            String password = (String)body.get("password");

            if (username==null || username.isEmpty()) {
                throw new UserException("Verifique o campo Usuário!");
            } else {
                username = username.trim().toLowerCase();
                if(!userService.usernameRegex(username)) {
                    throw new UserException("Nome de usuário está com formato inválido!");
                }
            }
            if (email==null || email.isEmpty()) {
                throw new UserException("Verifique o campo Email!");
            } else {
                email = email.trim().toLowerCase();
                if(!userService.emailRegex(email)) {
                    throw new UserException("Email está com formato inválido!");
                }
            }
            if (password==null || password.isEmpty()) {
                throw new UserException("Verifique o campo Senha!");
            } else {
                if(!userService.passwordRegex(password)) {
                    throw new UserException("Senha está com formato inválido!");
                }
            }

            if(userService.usernameExist(username)) {
                throw new UserException("Usuário \""+username+"\" já esta cadastrado!");
            }
            if(userService.emailExist(email)) {
                throw new UserException("Email \""+email+"\" já esta cadastrado!");
            }
            if(!GroupService.getInstance().emailAvailableForOrganization(email)) {
                throw new UserException("Email \""+email+"\" não esta disponível para cadastro!");
            }

            User user = new User();
            user.setName(username);
            user.setEmail(email);
            if(userService.confirmAccountEnabled()) {
                user.setInactive(true);
            }
            userService.setRawPasswordToUser(user, password, false);
            user.setOrganization(groupService.getOrganizationBasedInDomain());

            userService.createUser(user);

            if(userService.confirmAccountEnabled()) {
                userService.sendConfirmAccountEmail(user, true);
            }

            response.success = true;

            response.message = userService.confirmAccountEnabled() ? "Usuário registrado com sucesso, Enviamos um link de confirmação de conta para o seu email cadastrado." : "Usuário registrado com sucesso, efetue o login para continuar.";

            response.alertOptions.put("title", userService.confirmAccountEnabled() ? "Confirmação de Conta" : "Registro de Conta");
            response.alertOptions.put("icon", userService.confirmAccountEnabled() ? "info" : "success");
            response.alertOptions.put("modalAlert", true);
            response.alertOptions.put("timer", null);

        });
    }


    @PostMapping(value = "/account/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response account_edit(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String newPassword = (String)body.get("newPassword");
            if(newPassword == null || newPassword.isEmpty()) {
                throw new UserException("Parametro newPassword é nulo.");
            }

            if(!userService.passwordRegex(newPassword)) {
                throw new UserException("Nova Senha está com formato inválido!");
            }

            String password = (String)body.get("password");

            User user = userService.getUserInSession();

            // if logged with google don't check password
            boolean logadoComGoogle = (userService.getInSession("loginViaGoogle") != null);

            if (logadoComGoogle || userService.passwordValid(user, password)) {

                userService.setRawPasswordToUser(user, newPassword, false);

                userService.updateUserInSession();

                response.success = true;
                response.message = "As Alterações foram salvas com sucesso.";

            } else {
                throw new UserException("Credenciais Invalidas!");
            }

        });
    }

    @PostMapping(value = "/admin/account/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response admin_account_edit(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

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

            User userEdit = (User) userService.findFirstById(userId);
            if(userEdit == null) {
                throw new UserException("Usuário não encontrado.");
            }

            String usernameOld = userEdit.getUsername();

            if(username != null && !username.isEmpty()) {
                if(userService.usernameExist(username) && !username.equals(usernameOld)) {
                    throw new UserException("Usuário \""+username+"\" já esta cadastrado!");
                }
                if(userService.usernameRegex(username)) {
                    userEdit.setName(username);
                } else {
                    throw new UserException("Nome de Usuário está com formato inválido!");
                }
            }
            if(email != null && !email.isEmpty()) {
                if(userService.emailExist(email) && !email.equals(userEdit.getEmail())) {
                    throw new UserException("Email \""+email+"\" já esta cadastrado!");
                }
                if(userService.emailRegex(email)) {
                    userEdit.setEmail(email);
                } else {
                    throw new UserException("Email está com formato inválido!");
                }
            }
            if(password != null && !password.isEmpty()) {
                userService.setRawPasswordToUser(userEdit, password, false);
            }

            if(authorityLevel != null && !authorityLevel.isEmpty()) {
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

            response.success = true;
            response.message = "As Alterações foram salvas com sucesso, A sessão do usuário foi finalizada.";

        });
    }

    @PostMapping(value = "/login/google", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response login_google(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            if(!Boolean.parseBoolean(environment.getProperty("LOGIN_GOOGLE_ENABLED"))) {
                throw new UserException("Login via Google desabilitado!");
            }

            String idTokenString = (String)body.get("token");

            if(idTokenString==null) {
                throw new UserException("Parametro token é nulo.");
            }

            // check if payload is valid
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(environment.getProperty("GOOGLE_CLIENT_ID")))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            HttpSession sessionReq = userService.getActiveSession();

            if (idToken != null) {
                Payload payload = idToken.getPayload();

                //String userId = payload.getSubject();

                String email = payload.getEmail();
                if(email == null) {
                    throw new UserException("Não foi possível obter Email.");
                }

                if(!GroupService.getInstance().emailAvailableForOrganization(email)) {
                    throw new UserException("Email \""+email+"\" não esta disponível para cadastro!");
                }
                
                //boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");
                //String locale = (String) payload.get("locale");
                //String familyName = (String) payload.get("family_name");
                //String givenName = (String) payload.get("given_name");

                User user;

                try {
                    user = (User) userService.findFirstByEmail(email);
                } catch (UserException e) {
                    // register user with DCX account, with secure payload information

                    // create username from email DCX
                    String newUsername = email.split("@")[0].trim();
                    if(!userService.usernameExist(newUsername)) {

                        user = new User();
                        user.setName(newUsername);
                        user.setEmail(email.trim());
                        user.setOrganization(groupService.getOrganizationBasedInDomain());
                        userService.createUser(user);

                        Profile profile = user.getProfile();

                        if(name != null) {
                            // if have space, extract first name and last name
                            if(name.contains(" ")) {
                                String[] nameArr = name.split(" ");
                                profile.setFirstname((nameArr[0]).trim());
                                profile.setLastname(name.substring(nameArr[0].length()).trim());
                            } else {
                                profile.setFirstname(name.trim());
                            }
                        }
                        if(pictureUrl != null) {
                            profile.setImage(pictureUrl.trim());
                        }

                        profileService.save(profile);

                        userService.saveInSession("novoUsuario", true);

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

                    userService.saveInSession("loginViaGoogle", true);

                    userService.configureSessionForUser(user, authenticationManager);

                    response.success = true;
                    response.redirectTo = userService.getUrlWhenLogin();
                    response.message = "Usuário Logado com sucesso.";

                    response.token = jwtService.buildTokenForUser(user);

                    response.body.put("user", user);

                    return;
                }

            } else {
                throw new UserException("Token de Autenticação é Inválida.");
            }

            throw new UserException("Falha ao fazer login com Google.");

        });
    }

    // recovery user password
    @PostMapping(value = "/recovery-password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response recovery_password(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            response.alertOptions.put("title", "Recuperação de Senha");

            String usernameOrEmail = (String)body.get("username");

            if(usernameOrEmail == null) {
                throw new UserException("Parametro username é nulo.");
            }

            User user = null;

            try {
                user = (User) userService.loadUserByUsername(usernameOrEmail);
            } catch (Exception e) {
                throw new UserException("Conta não encontrada!");
            }

            userService.sendRecoveryPasswordEmail(user);

            response.message = "Enviamos um link de recuperação da senha para seu email cadastrado.";

            response.alertOptions.put("icon", "success");
            response.alertOptions.put("modalAlert", true);
            response.alertOptions.put("timer", null);

            response.redirectTo = "/login";
        });
    }

    // create new password for user
    @PostMapping(value = "/new-password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response new_password(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String token = (String)body.get("token");
            String newPassword = (String)body.get("newPassword");

            if(token == null) {
                throw new UserException("Parametro token é nulo.");
            }
            if(newPassword == null) {
                throw new UserException("Parametro newPassword é nulo.");
            }

            if(!userService.passwordRegex(newPassword)) {
                throw new UserException("Nova Senha está com formato inválido!");
            }

            User user = userService.getUserByRecoveryPasswordToken(token);

            if(user == null) {
                throw new UserException("Token de recuperação de senha inválido ou expirado!");
            }

            user.setRecoveryPasswordToken(null);
            user.setInactive(false);
            userService.setRawPasswordToUser(user, newPassword, true);



            response.message = "Senha alterada com sucesso, efetue o login para continuar.";
            response.redirectTo = "/login";

        });
    }

    // request confirm account
    @PostMapping(value = "/confirm-account", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response request_confirm_account() {
        return Response.buildResponse(response -> {

            response.alertOptions.put("title", "Confirmação de Conta");

            User user = userService.getUserInSession();

            if(userService.isAccountConfirmed(user)) {
                throw new UserException("Conta já confirmada!");
            }

            userService.sendConfirmAccountEmail(user, false);

            response.message = "Enviamos um link de confirmação de conta para seu email cadastrado.";

            response.alertOptions.put("icon", "info");
            response.alertOptions.put("modalAlert", true);
            response.alertOptions.put("timer", null);

        });
    }

    // confirm account
    @GetMapping(value = "/confirm-account/{token}")
    @ResponseBody
    public ResponseEntity confirm_account(@PathVariable("token")String token) throws Exception {

        URL requestUrl = new URL(userService.getRequest().getRequestURL().toString());

        String baseUrl = "https://" + requestUrl.getHost();

        User user = token==null ? null : userService.getUserByRecoveryPasswordToken(token);

        if(user == null) {
            userService.saveInSession("message_account_confirmed", "Token de confirmação de conta inválido ou expirado!");
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(baseUrl + "/login")).build();
        }

        user.setRecoveryPasswordToken(null);
        user.setInactive(false);
        user.setConfirmed(true);
        userService.save(user);

        userService.saveInSession("account_confirmed", true);
        userService.saveInSession("message_account_confirmed", "Conta confirmada com sucesso, efetue o login para continuar.");

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(baseUrl + "/login")).build();
    }



}