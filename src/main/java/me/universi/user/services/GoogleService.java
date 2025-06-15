package me.universi.user.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import me.universi.Sys;
import me.universi.group.entities.GroupEnvironment;
import me.universi.group.services.OrganizationService;
import me.universi.user.dto.LoginTokenDTO;
import me.universi.user.entities.User;
import me.universi.user.exceptions.UserException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GoogleService {

    private final LoginService loginService;

    @Value("${RECAPTCHA_API_KEY}")
    public String recaptchaApiKey;

    @Value("${RECAPTCHA_API_PROJECT_ID}")
    public String recaptchaApiProjectId;

    @Value("${RECAPTCHA_SITE_KEY}")
    public String recaptchaSiteKey;

    @Value("${RECAPTCHA_ENABLED}")
    public boolean captchaEnabled;

    @Value("${LOGIN_GOOGLE_ENABLED}")
    public boolean loginGoogleEnabled;

    @Value("${GOOGLE_CLIENT_ID}")
    public String googleClientId;

    public GoogleService(LoginService loginService) {
        this.loginService = loginService;
    }

    // bean instance via context
    public static GoogleService getInstance() {
        return Sys.context.getBean("googleService", GoogleService.class);
    }

    public User googleLogin(LoginTokenDTO loginTokenDTO ) {
        if(!isLoginViaGoogleEnabled()) {
            throw new UserException("Login via Google desabilitado!");
        }

        String idTokenString = loginTokenDTO.token();

        if(idTokenString==null) {
            throw new UserException("Parametro token é nulo.");
        }

        // check if payload is valid
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(getGoogleClientId()))
                .build();

        GoogleIdToken idToken = null;
        try {
            idToken = verifier.verify(idTokenString);
        } catch (Exception e) {
            throw new UserException("Ocorreu um erro ao verificar Token de Autenticação.");
        }

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");

            String username = email.split("@")[0].trim();

            User user = loginService.configureLoginForOAuth(name, username, email, pictureUrl);

            if(user != null) {
                return user;
            }

        } else {
            throw new UserException("Token de Autenticação é Inválida.");
        }

        throw new UserException("Falha ao fazer login com Google.");
    }

    public boolean isLoginViaGoogleEnabled() {
        GroupEnvironment envG = OrganizationService.getInstance().getEnvironment();
        if(envG != null) {
            return envG.login_google_enabled;
        }
        return loginGoogleEnabled;
    }

    public boolean isCaptchaEnabled() {
        GroupEnvironment envG = OrganizationService.getInstance().getEnvironment();
        if(envG != null) {
            return envG.recaptcha_enabled;
        }
        return captchaEnabled;
    }

    public String getGoogleClientId() {
        GroupEnvironment envG = OrganizationService.getInstance().getEnvironment();
        if(envG != null) {
            return envG.google_client_id;
        }
        return googleClientId;
    }


    public String getRecaptchaApiKey() {
        GroupEnvironment envG = OrganizationService.getInstance().getEnvironment();
        if(envG != null) {
            return envG.recaptcha_api_key;
        }
        return recaptchaApiKey;
    }

    public String getRecaptchaApiProjectId() {
        GroupEnvironment envG = OrganizationService.getInstance().getEnvironment();
        if(envG != null) {
            return envG.recaptcha_api_project_id;
        }
        return recaptchaApiProjectId;
    }

    public String getRecaptchaSiteKey() {
        GroupEnvironment envG = OrganizationService.getInstance().getEnvironment();
        if(envG != null) {
            return envG.recaptcha_site_key;
        }
        return recaptchaSiteKey;
    }

    public void checkRecaptchaWithToken(Object gToken) {
        if(isCaptchaEnabled()) {

            String recaptchaResponse = (String) gToken;

            if (recaptchaResponse == null || recaptchaResponse.isEmpty()) {
                throw new UserException("Recaptcha Requerido.");
            }

            String url = "https://recaptchaenterprise.googleapis.com/v1/projects/"+ getRecaptchaApiProjectId() +"/assessments?key=" + getRecaptchaApiKey();

            HashMap<String, Object> post = new HashMap<>();
            HashMap<String, Object> event = new HashMap<>();
            event.put("token", recaptchaResponse);
            event.put("siteKey", getRecaptchaSiteKey());
            event.put("expectedAction", "SUBMIT");
            post.put("event", event);

            RestTemplate restTemplate = new RestTemplate();
            HashMap<String, Object> responseCaptcha = restTemplate.postForObject(url, post, HashMap.class);

            boolean isTokenValid = false;

            try {
                isTokenValid = (((Double)((Map)responseCaptcha.get("riskAnalysis")).get("score")) >= 0.3);
            } catch (Exception ignored) {
            }

            if (!isTokenValid) {
                throw new UserException("Recaptcha inválido.");
            }
        }
    }
}