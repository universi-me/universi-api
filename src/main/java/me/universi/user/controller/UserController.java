package me.universi.user.controller;

import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import me.universi.api.entities.Response;
import me.universi.group.services.GroupService;
import me.universi.profile.services.ProfileService;
import me.universi.roles.services.RolesService;
import me.universi.user.entities.User;
import me.universi.user.enums.Authority;
import me.universi.user.exceptions.UserException;
import me.universi.user.services.JWTService;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import io.jsonwebtoken.Jwts;

@RestController
@RequestMapping(value = "/api")
public class UserController {
    private final UserService userService;
    private final ProfileService profileService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    @Autowired
    public UserController(UserService userService, ProfileService profileService, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.userService = userService;
        this.profileService = profileService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @GetMapping(value = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response account() {
        return Response.buildResponse(response -> {

            if(userService.userIsLoggedIn()) {
                response.success = true;
                response.body.put("user", userService.getUserInSession());
                response.body.put("roles", RolesService.getInstance().getAllRolesSession());
            } else {
                response.success = false;
                response.status = 401;
            }

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

            boolean usernameRegex = userService.usernameRegex(username);
            boolean usernameExist = userService.usernameExist(username);

            response.success = usernameRegex && !usernameExist;

            response.body.put("reason", !usernameRegex ? "Verifique o formato do nome de usuário." :
                                        usernameExist ? "Este nome de usuário está em uso." :
                                                "Usuário Disponível para uso.");
        });
    }

    @PostMapping(value = "/email-available", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response available_email_check(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            String email = (String)body.get("email");

            boolean emailRegex = userService.emailRegex(email);
            boolean emailExist = userService.emailExist(email);
            boolean emailAvailableForOrganization = GroupService.getInstance().emailAvailableForOrganization(email);

            response.success = emailRegex && !emailExist && emailAvailableForOrganization;

            response.body.put("reason", !emailRegex ? "Verifique o formato do email." :
                                        emailExist ? "Este email já está em uso." :
                                        !emailAvailableForOrganization ? "Email não autorizado.\nUtilize seu email corporativo." :
                                                "Email Disponível para uso.");
        });
    }

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response signup(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            response.alertOptions.put("title", "Registro de Conta");

            // check if register is enabled
            if(!userService.isSignupEnabled()) {
                throw new UserException("Registrar-se está desativado!");
            }

            userService.checkRecaptchaWithToken(body.get("recaptchaToken"));

            String username = (String)body.get("username");
            String email = (String)body.get("email");

            String firstname = (String)body.get("firstname");
            String lastname = (String)body.get("lastname");

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
            if(userService.isConfirmAccountEnabled()) {
                user.setInactive(true);
            }
            userService.saveRawPasswordToUser(user, password, false);

            userService.createUser(user, firstname, lastname);

            if(userService.isConfirmAccountEnabled()) {
                userService.sendConfirmAccountEmail(user, true);
            }

            response.success = true;

            response.message = userService.isConfirmAccountEnabled() ? "Usuário registrado com sucesso, Enviamos um link de confirmação de conta para o seu email cadastrado." : "Usuário registrado com sucesso, efetue o login para continuar.";

            response.alertOptions.put("title", userService.isConfirmAccountEnabled() ? "Confirmação de Conta" : "Registro de Conta");
            response.alertOptions.put("icon", userService.isConfirmAccountEnabled() ? "info" : "success");
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

                userService.saveRawPasswordToUser(user, newPassword, false);

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

            if(!userService.isUserAdminSession()) {
                throw new UserException("Você não tem permissão para editar usuário.");
            }

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
                userService.saveRawPasswordToUser(userEdit, password, false);
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

    @PostMapping(value = "/admin/account/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response admin_account_list(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            if(!userService.isUserAdminSession()) {
                throw new UserException("Você não tem permissão para listar usuários.");
            }

            Object byRole = body.get("accessLevel");

            response.success = true;
            response.body.put("users", userService.findAllUsers(byRole));

        });
    }

    @PostMapping(value = "/login/keycloak", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response oauth_keycloak_session(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            if(!userService.KEYCLOAK_ENABLED) {
                throw new UserException("Keycloak desabilitado!");
            }

            String code = (String)body.get("code");
            if(code == null) {
                throw new UserException("Parametro code é nulo.");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED.toString());
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
            requestBody.add("client_id", userService.KEYCLOAK_CLIENT_ID);
            requestBody.add("grant_type", "authorization_code");
            requestBody.add("redirect_uri", userService.keycloakRedirectUrl());
            requestBody.add("client_secret", userService.KEYCLOAK_CLIENT_SECRET);
            requestBody.add("code", code);
            HttpEntity formEntity = new HttpEntity<MultiValueMap<String, String>>(requestBody, headers);

            RestTemplate restTemplate = new RestTemplate();
            HashMap<String, Object> token = restTemplate.postForObject(userService.KEYCLOAK_URL + "/realms/"+ userService.KEYCLOAK_REALM +"/protocol/openid-connect/token", formEntity, HashMap.class);

            // returned secured token
            String accessToken = (String)token.get("access_token");

            Map<String, Object> decodedToken = Jwts.parser()
                    .parseClaimsJwt(accessToken.substring(0, accessToken.lastIndexOf('.') + 1))
                    .getBody();

            String email = (String)decodedToken.get("email");
            String username = (String)decodedToken.get("preferred_username");
            String name = (String)decodedToken.get("name");
            String pictureUrl = null;

            User user = userService.configureLoginForOAuth(name, username, email, pictureUrl);

            if(user != null) {
                response.success = true;
                response.redirectTo = userService.getUrlWhenLogin();
                response.message = "Usuário Logado com sucesso.";

                response.token = jwtService.buildTokenForUser(user);

                response.body.put("user", user);

                return;
            }

            throw new UserException("Falha ao fazer login com Keycloak.");

        });
    }

    @GetMapping(value = "/login/keycloak/auth")
    @ResponseBody
    public ResponseEntity<Void> keycloak_login() {
        if(!userService.KEYCLOAK_ENABLED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "denied access to keycloak login");
        }
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(userService.keycloakLoginUrl())).build();
    }

    @PostMapping(value = "/login/google", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response login_google(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            if(!userService.isLoginViaGoogleEnabled()) {
                throw new UserException("Login via Google desabilitado!");
            }

            String idTokenString = (String)body.get("token");

            if(idTokenString==null) {
                throw new UserException("Parametro token é nulo.");
            }

            // check if payload is valid
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(userService.getGoogleClientId()))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                Payload payload = idToken.getPayload();

                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");

                String username = email.split("@")[0].trim();

                User user = userService.configureLoginForOAuth(name, username, email, pictureUrl);

                if(user != null) {

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

            userService.checkRecaptchaWithToken(body.get("recaptchaToken"));

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
            userService.saveRawPasswordToUser(user, newPassword, true);



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