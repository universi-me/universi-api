package me.universi.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import me.universi.api.entities.Response;
import me.universi.user.dto.*;
import me.universi.user.entities.User;
import me.universi.user.exceptions.UserException;
import me.universi.user.services.JWTService;
import me.universi.user.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "")
public class UserController {
    private final UserService userService;
    private final JWTService jwtService;

    @Autowired
    public UserController(UserService userService, JWTService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping(value = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetAccountDTO> account() {
        GetAccountDTO getAccountDTO = userService.getAccountSession();
        return getAccountDTO != null ? ResponseEntity.ok(getAccountDTO) : new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PatchMapping(value = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> account_edit( @Valid @RequestBody UpdateAccountDTO updateAccountDTO) {
        userService.editAccount( updateAccountDTO );
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/logout")
    public ResponseEntity<Boolean> logout() {
        return ResponseEntity.ok( userService.logoutUserSession() );
    }

    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> signup( @Valid @RequestBody CreateAccountDTO createAccountDTO ) {
        return ResponseEntity.ok( userService.createAccount( createAccountDTO ) );
    }

    @GetMapping(value = "/available/username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetAvailableCheckDTO> available_username_check( @Valid @PathVariable @NotNull( message = "username inválido" ) String username ) {
        return ResponseEntity.ok( userService.availableUsernameCheck( username ) );
    }

    @GetMapping(value = "/available/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetAvailableCheckDTO> available_email_check( @Valid @PathVariable @NotNull( message = "email inválido" ) String email ) {
        return ResponseEntity.ok( userService.availableEmailCheck( email ) );
    }

    @PatchMapping(value = "/admin/account", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> admin_account_edit( @Valid @RequestBody EditAccountDTO editAccountDTO ) {
        userService.adminEditAccount( editAccountDTO );
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/admin/accounts/{accessLevel}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> admin_account_list( @Valid @PathVariable @NotNull( message = "accessLevel inválido" ) String accessLevel ) {
        return userService.adminListAccount( accessLevel );
    }

    @PostMapping(value = "/login/keycloak", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response oauth_keycloak_session(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            if(!userService.isKeycloakEnabled()) {
                throw new UserException("Keycloak desabilitado!");
            }

            String code = (String)body.get("code");
            if(code == null) {
                throw new UserException("Parametro code é nulo.");
            }

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED.toString());
                headers.add("Accept", MediaType.APPLICATION_JSON.toString());

                MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
                requestBody.add("client_id", userService.getKeycloakClientId());
                requestBody.add("grant_type", "authorization_code");
                requestBody.add("redirect_uri", userService.getKeycloakRedirectUrl());
                requestBody.add("client_secret", userService.getKeycloakClientSecret());
                requestBody.add("code", code);
                HttpEntity formEntity = new HttpEntity<MultiValueMap<String, String>>(requestBody, headers);

                RestTemplate restTemplate = new RestTemplate();
                HashMap<String, Object> token = restTemplate.postForObject(userService.getKeycloakUrl() + "/realms/" + userService.getKeycloakRealm() + "/protocol/openid-connect/token", formEntity, HashMap.class);

                // returned secured token
                String accessToken = (String) token.get("access_token");

                // Split the JWT into its parts
                String[] parts = accessToken.split("\\.");
                if (parts.length != 3) {
                    throw new IllegalArgumentException("Invalid JWT token");
                }

                String bodyJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
                Map<String, Object> decodedToken = new ObjectMapper().readValue(bodyJson, Map.class);

                String email = (String) decodedToken.get("email");
                String username = (String) decodedToken.get("preferred_username");
                String name = (String) decodedToken.get("name");
                String pictureUrl = null;

                User user = userService.configureLoginForOAuth(name, username, email, pictureUrl);

                if (user != null) {
                    response.success = true;
                    response.redirectTo = userService.getUrlWhenLogin();
                    response.message = "Usuário Logado com sucesso.";

                    response.token = jwtService.buildTokenForUser(user);

                    response.body.put("user", user);

                    return;
                }
            }catch (Exception e) {
                if(e.getClass() == UserException.class) {
                    throw e;
                }
                response.success = false;
                return;
            }

            throw new UserException("Falha ao fazer login com Keycloak.");

        });
    }

    @GetMapping(value = "/login/keycloak/auth")
    public ResponseEntity<Void> keycloak_login() {
        if(!userService.isKeycloakEnabled()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "denied access to keycloak login");
        }
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(userService.keycloakLoginUrl())).build();
    }

    @PostMapping(value = "/login/google", produces = MediaType.APPLICATION_JSON_VALUE)
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
    @PostMapping(value = "/recovery-password", produces = MediaType.APPLICATION_JSON_VALUE)
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
    @PostMapping(value = "/new-password", produces = MediaType.APPLICATION_JSON_VALUE)
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
    @PostMapping(value = "/confirm-account", produces = MediaType.APPLICATION_JSON_VALUE)
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