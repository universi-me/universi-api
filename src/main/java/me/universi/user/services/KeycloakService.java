package me.universi.user.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import me.universi.Sys;
import me.universi.group.entities.GroupEnvironment;
import me.universi.group.services.OrganizationService;
import me.universi.user.dto.LoginCodeDTO;
import me.universi.user.dto.LoginResponseDTO;
import me.universi.user.entities.User;
import me.universi.user.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class KeycloakService {
    private final RequestService requestService;
    private final LoginService loginService;

    @Value("${keycloak.enabled}")
    boolean KEYCLOAK_ENABLED;
    @Value("${keycloak.auth-server-url}")
    String KEYCLOAK_URL;
    @Value("${keycloak.redirect-url}")
    String KEYCLOAK_REDIRECT_URL;
    @Value("${keycloak.realm}")
    String KEYCLOAK_REALM;
    @Value("${keycloak.client-id}")
    String KEYCLOAK_CLIENT_ID;
    @Value("${keycloak.client-secret}")
    String KEYCLOAK_CLIENT_SECRET;

    public KeycloakService(RequestService requestService, LoginService loginService) {
        this.requestService = requestService;
        this.loginService = loginService;
    }

    // bean instance via context
    public static KeycloakService getInstance() {
        return Sys.context().getBean("keycloakService", KeycloakService.class);
    }

    public String keycloakLoginUrl() {
        return  getKeycloakUrl() + "/realms/" + getKeycloakRealm() + "/protocol/openid-connect/auth?client_id=" + getKeycloakClientId() + "&redirect_uri="+ getKeycloakRedirectUrl() +"&response_type=code";
    }

    public String urlDefaultKeycloakRedirectCallback() {
        return requestService.getRefererUrlBase() + "/keycloak-oauth-redirect";
    }

    public String getKeycloakRedirectUrl() {
        GroupEnvironment envG = OrganizationService.getInstance().getEnvironment();
        if(envG != null && envG.keycloak_redirect_url != null && !envG.keycloak_redirect_url.isEmpty()) {
            return envG.keycloak_redirect_url;
        }
        if(KEYCLOAK_REDIRECT_URL != null && !KEYCLOAK_REDIRECT_URL.isEmpty()){
            return KEYCLOAK_REDIRECT_URL;
        }
        return urlDefaultKeycloakRedirectCallback();
    }

    public String getKeycloakClientId() {
        GroupEnvironment envG = OrganizationService.getInstance().getEnvironment();
        if(envG != null && envG.keycloak_client_id != null && !envG.keycloak_client_id.isEmpty()) {
            return envG.keycloak_client_id;
        }
        return KEYCLOAK_CLIENT_ID;
    }

    public String getKeycloakClientSecret() {
        GroupEnvironment envG = OrganizationService.getInstance().getEnvironment();
        if(envG != null && envG.keycloak_client_secret != null && !envG.keycloak_client_secret.isEmpty()) {
            return envG.keycloak_client_secret;
        }
        return KEYCLOAK_CLIENT_SECRET;
    }

    public String getKeycloakRealm() {
        GroupEnvironment envG = OrganizationService.getInstance().getEnvironment();
        if(envG != null && envG.keycloak_realm != null && !envG.keycloak_realm.isEmpty()) {
            return envG.keycloak_realm;
        }
        return KEYCLOAK_REALM;
    }

    public String getKeycloakUrl() {
        GroupEnvironment envG = OrganizationService.getInstance().getEnvironment();
        if(envG != null && envG.keycloak_url != null && !envG.keycloak_url.isEmpty()) {
            return envG.keycloak_url.replaceAll("/$", "");
        }
        return KEYCLOAK_URL;
    }

    public boolean isKeycloakEnabled() {
        GroupEnvironment envG = OrganizationService.getInstance().getEnvironment();
        if(envG != null) {
            return envG.keycloak_enabled;
        }
        return KEYCLOAK_ENABLED;
    }

    public URI getKeycloakLoginUrl() {
        if(!isKeycloakEnabled()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "denied access to keycloak login");
        }
        return URI.create(keycloakLoginUrl());
    }

    public LoginResponseDTO keycloakLogin(LoginCodeDTO loginCodeDTO) {
        if(!isKeycloakEnabled()) {
            throw new UserException("Keycloak desabilitado!");
        }

        String code = loginCodeDTO.code();
        if(code == null) {
            throw new UserException("Parametro token é nulo.");
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED.toString());
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
            requestBody.add("client_id", getKeycloakClientId());
            requestBody.add("grant_type", "authorization_code");
            requestBody.add("redirect_uri", getKeycloakRedirectUrl());
            requestBody.add("client_secret", getKeycloakClientSecret());
            requestBody.add("code", code);
            HttpEntity formEntity = new HttpEntity<MultiValueMap<String, String>>(requestBody, headers);

            RestTemplate restTemplate = new RestTemplate();
            HashMap<String, Object> token = restTemplate.postForObject(getKeycloakUrl() + "/realms/" + getKeycloakRealm() + "/protocol/openid-connect/token", formEntity, HashMap.class);

            // returned secured token
            String accessToken = (String) token.get("access_token");

            // Split the JWT into its parts
            String[] parts = accessToken.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token");
            }

            String bodyJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            Map<String, Object> decodedToken = new ObjectMapper().readValue(bodyJson, Map.class);

            String email = (String) decodedToken.get("email");
            String username = (String) decodedToken.get("preferred_username");
            String name = (String) decodedToken.get("name");
            String pictureUrl = (String) decodedToken.get("picture");

            User user = loginService.configureLoginForOAuth(name, username, email, pictureUrl);

            if (user != null) {
                return new LoginResponseDTO(user, JWTService.getInstance().buildTokenForUser(user, true));
            }
        } catch (Exception e) {
            if(e.getClass() == UserException.class) {
                throw (UserException) e;
            }
        }

        throw new UserException("Falha ao fazer login com Keycloak.");
    }
}