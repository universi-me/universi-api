package me.universi.user.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import me.universi.Sys;
import me.universi.api.interfaces.EntityService;
import me.universi.group.entities.Group;
import me.universi.group.entities.GroupSettings.GroupEnvironment;
import me.universi.group.services.GroupService;
import me.universi.image.services.ImageMetadataService;
import me.universi.profile.entities.Profile;
import me.universi.profile.exceptions.ProfileException;
import me.universi.profile.repositories.PerfilRepository;
import me.universi.role.services.RoleService;
import me.universi.user.dto.*;
import me.universi.user.entities.User;
import me.universi.user.enums.Authority;
import me.universi.user.exceptions.ExceptionResponse;
import me.universi.user.exceptions.UserException;
import me.universi.user.repositories.UserRepository;
import me.universi.util.CastingUtil;
import me.universi.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class UserService extends EntityService<User> implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PerfilRepository profileRepository;
    private final RoleHierarchyImpl roleHierarchy;
    private final SessionRegistry sessionRegistry;
    private JavaMailSender emailSender;
    private final Executor emailExecutor;

    public String BUILD_HASH = "development";

    @Value("${PUBLIC_URL}")
    public String PUBLIC_URL;

    @Value("${BUILD_HASH}")
    public String BUILD_HASH_ENV;

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

    @Value("${SIGNUP_ENABLED}")
    public boolean signupEnabled;

    @Value("${SIGNUP_CONFIRMATION_ENABLED}")
    public boolean signupConfirmationEnabled;

    @Value("${spring.profiles.active}")
    public String activeProfile;

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

    @Value( "${server.servlet.context-path}" )
    private String contextPath;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, PerfilRepository profileRepository, RoleHierarchyImpl roleHierarchy, SessionRegistry sessionRegistry) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileRepository = profileRepository;
        this.roleHierarchy = roleHierarchy;
        this.sessionRegistry = sessionRegistry;
        this.emailExecutor = Executors.newFixedThreadPool(5);
    }

    // UserService bean instance via context
    public static UserService getInstance() {
        return Sys.context.getBean("userService", UserService.class);
    }

    @Override
    public Optional<User> find( UUID id ) {
        return userRepository.findById( id );
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findByUsername( String username ) {
        var organization = GroupService.getInstance().obtainOrganizationBasedInDomain();

        return organization == null
            ? userRepository.findFirstByName( username )
            : userRepository.findFirstByNameAndOrganizationId( username, organization.getId() );
    }

    public Optional<User> findByEmail( String email ) {
        var organization = GroupService.getInstance().obtainOrganizationBasedInDomain();
        return organization == null
            ? userRepository.findFirstByEmail( email )
            : userRepository.findFirstByEmailAndOrganizationId( email, organization.getId() );
    }

    @Override
    public User loadUserByUsername( String username ) throws UsernameNotFoundException {
        return findByUsernameOrEmail( username )
            .orElseThrow( () -> new UsernameNotFoundException("Usuário não encontrado!") );
    }

    public Optional<User> findByUsernameOrEmail( String usernameOrEmail ) {
        var organization = GroupService.getInstance().obtainOrganizationBasedInDomain();
        return organization == null
            ? userRepository.findFirstByEmailOrName( usernameOrEmail )
            : userRepository.findFirstByEmailOrNameAndOrganizationId( usernameOrEmail, organization.getId() );
    }

    public void createUser(User user, String firstname, String lastname) throws UserException {
        if (user==null) {
            throw new UserException("Usuario está vazio!");
        } else if (user.getUsername()==null) {
            throw new UserException("username está vazio!");
        }

        user.setAuthority(Authority.ROLE_USER);
        save(user);

        if(user.getProfile() == null) {
            Profile userProfile = new Profile();

            if(firstname != null) {
                String nameString = String.valueOf(firstname);
                if(nameString.length() > 50) {
                    throw new ProfileException("O nome não pode ter mais de 50 caracteres.");
                }
                if(!nameString.isEmpty()) {
                    userProfile.setFirstname(nameString);
                }
            }
            if(lastname != null) {
                String lastnameString = String.valueOf(lastname);
                if(lastnameString.length() > 50) {
                    throw new ProfileException("O sobrenome não pode ter mais de 50 caracteres.");
                }
                if(!lastnameString.isEmpty()) {
                    userProfile.setLastname(lastnameString);
                }
            }

            userProfile.setUser(user);
            profileRepository.saveAndFlush(userProfile);
            try {
                // add organization to user profile
                GroupService groupService = GroupService.getInstance();
                groupService.addParticipantToGroup(groupService.getOrganizationBasedInDomain(), userProfile);
            } catch (Exception ignored) {
            }
            user.setProfile(userProfile);
        }
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean usernameExist(String username) {
        return findByUsername( username ).isPresent();
    }

    public boolean emailExist(String email) {
        return findByEmail( email ).isPresent();
    }

    public boolean usernameRegex(String username) {
        return matchRegex(username, "^[a-z0-9_.-].{1,49}$");
    }

    public boolean passwordRegex(String password) {
        return matchRegex(password, "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[^A-Za-zçÇ]).{8,255}$");
    }

    public boolean emailRegex(String email) {
        return matchRegex(email, "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$");
    }

    public boolean matchRegex(String input, String expression) {
        try {
            return Pattern.compile(expression).matcher(input).find();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean passwordValid(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public void checkPassword(User user, String rawPassword) throws UserException {
        if(!passwordValid(user, rawPassword)) {
            throw new UserException("Credenciais inválidas!");
        }
    }

    public void checkPasswordInSession(Object rawPassword) throws UserException {
        User userSession = getUserInSession();
        if(userSession.getPassword() == null || userSession.getPassword().isEmpty()) {
            return;
        }
        if(rawPassword == null || String.valueOf(rawPassword).isEmpty()) {
            throw new UserException("Requerido autenticar-se por senha!");
        }
        checkPassword(userSession, String.valueOf(rawPassword));
    }

    public void save(User user) {
        if(user.getOrganization() == null) {
            user.setOrganization(GroupService.getInstance().getOrganizationBasedInDomain());
        }
        userRepository.saveAndFlush(user);
    }

    public boolean isSessionOfUser(User user) {
        try {
            return (user.getUsername() != null && Objects.equals(getUserInSession().getUsername(), user.getUsername()));
        } catch (Exception e) {
            return false;
        }
    }

    public HttpServletRequest getRequest() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest();
    }

    public HttpSession getActiveSession() {
        HttpSession session = getRequest().getSession(true);
        Object domain = session.getAttribute("domain");
        if (domain != null) {
            if(!Objects.equals(domain, getDomainFromRequest())) {
                // force logout if session is invalid, due to not same domain
                throwErrorSessionInvalidAndRedirectToLogin();
            }
        }
        return session;
    }

    public String getActiveUrl() {
        return  getRequest().getRequestURI();
    }

    public void updateUserInSession() {
        User userSession = getUserInSession();
        if(userSession != null) {
            User actualUser = find(userSession.getId()).orElse(null);
            if(actualUser != null) {
                configureSessionForUser(actualUser, null);
            }
        }
    }

    // get host from request
    public String getDomainFromRequest() {
        try {
            return getRequest().getServerName();
        } catch (Exception e) {
            return null;
        }
    }

    // save in session based in domain
    public void saveInSession(String key, Object value) {
        HttpSession session = getActiveSession();
        session.setAttribute(key, value);
    }

    // get in session based in domain
    public Object getInSession(String key) {
        HttpSession session = getActiveSession();
        return session.getAttribute(key);
    }

    // remove in session based in domain
    public void removeInSession(String key) {
        HttpSession session = getActiveSession();
        session.removeAttribute(key);
    }

    public User getUserInSession() {
        HttpSession session = getActiveSession();
        if(session != null) {
            User user = (User) getInSession("user");
            if(user != null) {
                return user;
            }
        }
        if(userIsLoggedIn()) {
            // force logout if session is invalid, due to logged with no user
            throwErrorSessionInvalidAndRedirectToLogin();
        }
        return null;
    }

    public void throwErrorSessionInvalidAndRedirectToLogin() {
        logout();
        throw new ExceptionResponse("Sessão inválida!", "/login");
    }

    public void configureSessionForUser(User user, AuthenticationManager authenticationManager) {
        HttpSession session = getActiveSession();
        // set user session inactivity to 6 months
        session.setMaxInactiveInterval(6 * 30 * 24 * 60 * 60);

        // save domain in session
        saveInSession("domain", getDomainFromRequest());

        // save user based in domain in session
        saveInSession("user", user);

        // force authentication
        if(authenticationManager != null) {
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attr.getRequest();
            PreAuthenticatedAuthenticationToken preAuthenticatedAuthenticationToken = new PreAuthenticatedAuthenticationToken(user, user.getUsername(), AuthorityUtils.createAuthorityList(user.getAuthority().name()));
            preAuthenticatedAuthenticationToken.setDetails(new WebAuthenticationDetails(request));
            preAuthenticatedAuthenticationToken.setAuthenticated(false);
            Authentication authentication = authenticationManager.authenticate(preAuthenticatedAuthenticationToken);
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
            SecurityContextHolder.setContext(securityContext);
        }
    }

    public boolean userIsLoggedIn() {
        try {
            return SecurityContextHolder.getContext().getAuthentication() != null &&
                    SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                    !(SecurityContextHolder.getContext().getAuthentication()
                            instanceof AnonymousAuthenticationToken);
        } catch (Exception e) {
            return false;
        }
    }

    // check if user has authority following springsecurity hierarchy
    public boolean userHasAuthority(User user, Authority authority, boolean equal) {
        if(equal) {
            return user.getAuthority().equals(authority);
        }
        Collection<? extends GrantedAuthority> reachableRoles = roleHierarchy.getReachableGrantedAuthorities(user.getAuthorities());
        return reachableRoles.contains(new SimpleGrantedAuthority(authority.toString()));
    }

    public boolean isUserRole(User user, Authority role, boolean equal) {
        try {
            return userHasAuthority(user, role, equal);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isUserAdmin(User userSession) {
        return isUserRole(userSession, Authority.ROLE_ADMIN, false);
    }

    public boolean isUserDev(User userSession) {
        return isUserRole(userSession, Authority.ROLE_DEV, false);
    }

    public boolean isUserAdminSession() {
        return isUserAdmin(getUserInSession());
    }

    public boolean isUserDevSession() {
        return isUserDev(getUserInSession());
    }

    public boolean userNeedAnProfile(User user, boolean checkAdmin) {
        try {
            if(checkAdmin && isUserAdmin(user)) {
                return false;
            }
            return user.getProfile() == null
                || user.getProfile().getFirstname() == null
                || user.getProfile().getLastname() == null;
        } catch (Exception e) {
            return true;
        }
    }

    public String getLastSpringSecurityError(Exception exception) {
        String error = null;
        if (exception instanceof BadCredentialsException) {
            error = "Email ou Senha Inválidos";
        } else if (exception instanceof DisabledException) {
            error = "Conta Inativa, Tente recuperar conta.";
        } else if (exception instanceof AccountExpiredException) {
            error = "Conta Expirada";
        } else if (exception instanceof LockedException) {
            error = "Conta Bloqueada";
        } else if (exception instanceof CredentialsExpiredException) {
            error = "Conta com Credenciais Expirada";
        } else if(exception != null) {
            error = exception.getLocalizedMessage();
        }
        return error;
    }

    public String manageProfilePath() {
        return "/manage-profile";
    }

    // url redirect when user login
    public String getUrlWhenLogin() {

        User userSession = getUserInSession();
        if (userSession != null) {
            if(userNeedAnProfile(userSession, true)) {
                // go to user profile edit
                return manageProfilePath();
            } else {
                try {
                    return "/group" + userSession.getOrganization().getPath();
                } catch (Exception e) {
                    return "/profile/" + userSession.getUsername();
                }
            }
        }

        HttpSession session = getActiveSession();
        SavedRequest lastRequestSaved = (SavedRequest)session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        if(lastRequestSaved != null) {
            // return last request url user tried to access
            return lastRequestSaved.getRedirectUrl();
        }

        return "/";
    }

    // logout user remotely
    public void logoutUsername(String username) {
        for (Object principal: sessionRegistry.getAllPrincipals()) {
            if (principal instanceof UserDetails) {
                String usernameNow = ((UserDetails) principal).getUsername();
                if(usernameNow.equals(username))  {
                    for(SessionInformation sInfo : sessionRegistry.getAllSessions(principal, true)) {
                        sInfo.expireNow();
                    }
                    break;
                }
            }
        }
    }

    // check if user has active session
    public boolean isUsernameOnline(String username) {
        for (Object principal: sessionRegistry.getAllPrincipals()) {
            if (principal instanceof UserDetails) {
                String usernameNow = ((UserDetails) principal).getUsername();
                if(usernameNow.equals(username))  {
                    List<SessionInformation> activeSessions = sessionRegistry.getAllSessions(principal, false);
                    if(activeSessions != null && !activeSessions.isEmpty()) {
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }

    // logout user from current session
    public void logout() {
        try {
            SecurityContextHolder.clearContext();
            HttpSession session = getRequest().getSession(true);
            session.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUrlWhenLogout() {
        return "/login";
    }

    public void saveRawPasswordToUser(User user, String rawPassword, boolean logout) throws UserException {
        if(!passwordRegex(rawPassword)) {
            throw new UserException("Senha está com formato inválido!");
        }
        user.setPassword(encodePassword(rawPassword));
        user.setExpired_credentials(false);
        save(user);
        if(logout) {
            logoutUsername(user.getUsername());
        }
    }

    public void sendSystemEmailToUser(UserDetails user, String subject, String text, boolean ignoreEmailUnavailable) throws UserException {

        if(getEmailSender() == null) {
            return;
        }

        String email = ((User) user).getEmail();
        if (email == null) {
            if(ignoreEmailUnavailable) {
                return;
            } else {
                throw new UserException("Usuário não possui um email.");
            }
        }

        sendEmail(email, subject, text);
    }

    private void sendEmail(String email, String subject, String htmlContent) {
        emailExecutor.execute(() -> {
            try {
                MimeMessage message = getEmailSender().createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(email);
                helper.setSubject(subject);
                helper.setText(htmlContent, true);
                getEmailSender().send(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // generate recovery password token sha256 for user
    public String generateRecoveryPasswordToken(User user, boolean useIntervalCheck) throws UserException {

        //check recovery date token if less than 15min
        if(useIntervalCheck && user.getRecoveryPasswordTokenDate() != null) {
            long diff = ConvertUtil.getDateTimeNow().getTime() - user.getRecoveryPasswordTokenDate().getTime();
            if(diff < 900000) {
                throw new UserException("Um email de recuperação de senha já foi enviado para esta conta, por favor tente novamente mais tarde.");
            }
        }

        String tokenRandom = UUID.randomUUID().toString();

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new UserException("Algoritmo sha256 não disponível.");
        }
        byte[] encodedHash = digest.digest(tokenRandom.getBytes(StandardCharsets.UTF_8));
        String tokenString = ConvertUtil.bytesToHex(encodedHash);

        user.setRecoveryPasswordToken(tokenString);
        if(useIntervalCheck) {
            user.setRecoveryPasswordTokenDate(ConvertUtil.getDateTimeNow());
        }

        save(user);

        return tokenString;
    }

    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    public String getClientIpAddress() {
        HttpServletRequest request = getRequest();
        for (String header: IP_HEADER_CANDIDATES) {
            String ipList = request.getHeader(header);
            if (ipList != null && ipList.length() != 0 && !"unknown".equalsIgnoreCase(ipList)) {
                String ip = ipList.split(",")[0];
                return ip;
            }
        }
        return getRequest().getRemoteAddr();
    }

    // send recovery password email to user
    public void sendRecoveryPasswordEmail(User user) throws UserException {
        String userIp = getClientIpAddress();

        String token = generateRecoveryPasswordToken(user, true);

        String url = getPublicUrl() + "/recovery-password/" + token;
        String subject = "Universi.me - Recuperação de Senha";
        String text = "Olá " + user.getUsername() + ",<br/><br/>\n\n" +
                "Você solicitou a recuperação de senha para sua conta no Universi.me.<br/>\n" +
                "Para recuperar sua senha, clique no link abaixo:<br/><br/>\n\n" +
                url + "<br/><br/>\n\n" +
                "Se você não solicitou a recuperação de senha, por favor, ignore este email.<br/><br/>\n\n" +
                "Endereço IP: " + userIp + "<br/><br/>\n\n" +
                "Atenciosamente,<br/>\n" +
                "Equipe Universi.me";

        sendSystemEmailToUser(user, subject, text, false);
    }

    public User getUserByRecoveryPasswordToken(String token) {
        return userRepository.findFirstByRecoveryPasswordToken(token).orElse(null);
    }

    //send confirmation signup account email to user
    public void sendConfirmAccountEmail(User user, boolean signup) throws UserException {
        String userIp = getClientIpAddress();

        String token = generateRecoveryPasswordToken(user, false);

        String url = getPublicUrl() + "/confirm-account/" + token;
        String subject = "Universi.me - Confirmação de Conta";
        String messageExplain = (signup) ? "Seja bem-vindo(a) ao Universi.me, para continuar com o seu cadastro precisamos confirmar a sua conta do Universi.me." : "Você solicitou a confirmação de sua conta no Universi.me.";
        String text = "Olá " + user.getUsername() + ",<br/><br/>\n\n" +
                messageExplain + "<br/><br/>\n\n" +
                "Para confirmar sua conta, clique no link abaixo:<br/><br/>\n\n" +
                url + "<br/><br/>\n\n" +
                "Se você não solicitou a confirmação de conta, por favor, ignore este email.<br/><br/>\n\n" +
                "Endereço IP: " + userIp + "<br/><br/>\n\n" +
                "Atenciosamente,<br/>\n" +
                "Equipe Universi.me";

        sendSystemEmailToUser(user, subject, text, false);
    }

    public String getRecaptchaApiKey() {
        GroupEnvironment envG = GroupService.getInstance().getOrganizationEnvironment();
        if(envG != null) {
            return envG.recaptcha_api_key;
        }
        return recaptchaApiKey;
    }

    public String getRecaptchaApiProjectId() {
        GroupEnvironment envG = GroupService.getInstance().getOrganizationEnvironment();
        if(envG != null) {
            return envG.recaptcha_api_project_id;
        }
        return recaptchaApiProjectId;
    }

    public String getRecaptchaSiteKey() {
        GroupEnvironment envG = GroupService.getInstance().getOrganizationEnvironment();
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

    // is account confirmed
    public boolean isAccountConfirmed(User user) {
        return user.isConfirmed();
    }

    public boolean isConfirmAccountEnabled() {
        GroupEnvironment envG = GroupService.getInstance().getOrganizationEnvironment();
        if(envG != null) {
            return envG.signup_confirm_account_enabled;
        }
        return signupConfirmationEnabled;
    }

    public boolean isSignupEnabled() {
        GroupEnvironment envG = GroupService.getInstance().getOrganizationEnvironment();
        if(envG != null) {
            return envG.signup_enabled;
        }
        return signupEnabled;
    }

    public boolean isLoginViaGoogleEnabled() {
        GroupEnvironment envG = GroupService.getInstance().getOrganizationEnvironment();
        if(envG != null) {
            return envG.login_google_enabled;
        }
        return loginGoogleEnabled;
    }

    public boolean isCaptchaEnabled() {
        GroupEnvironment envG = GroupService.getInstance().getOrganizationEnvironment();
        if(envG != null) {
            return envG.recaptcha_enabled;
        }
        return captchaEnabled;
    }

    public boolean isProduction() {
        return "prod".equals(activeProfile);
    }

    public String getGoogleClientId() {
        GroupEnvironment envG = GroupService.getInstance().getOrganizationEnvironment();
        if(envG != null) {
            return envG.google_client_id;
        }
        return googleClientId;
    }

    public List<User> findAllUsers(Object byROLE) {
        Group organization = GroupService.getInstance().getOrganizationBasedInDomainIfExist();
        if(byROLE != null && !String.valueOf(byROLE).isEmpty()) {
            return organization == null ? userRepository.findAllByAuthority(Authority.valueOf(String.valueOf(byROLE))) : userRepository.findAllByAuthorityAndOrganizationId(Authority.valueOf(String.valueOf(byROLE)), organization.getId());
        }
        return organization == null ? userRepository.findAll() : userRepository.findAllByOrganizationId(organization.getId());
    }

    public String getBuildHash() {
        if(BUILD_HASH == null || BUILD_HASH.isEmpty() || "development".equals(BUILD_HASH)) {
            String jarPath = new File(".").getPath();
            String filePath = Paths.get(jarPath, "build.hash").toString();
            Resource resource = new FileSystemResource(filePath);
            try {
                if(resource.contentLength() == 0) {
                    return "development";
                }
                InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
                BUILD_HASH = FileCopyUtils.copyToString(reader);
            } catch (IOException ignored) {
            }
        }
        if("development".equals(BUILD_HASH) && BUILD_HASH_ENV != null && !BUILD_HASH_ENV.isEmpty()) {
            return BUILD_HASH_ENV;
        }
        return BUILD_HASH;
    }

    public User configureLoginForOAuth(String name, String username, String email, String pictureUrl) throws UserException {
        if(email == null) {
            throw new UserException("Não foi possível obter Email.");
        }
        if(username == null) {
            throw new UserException("Não foi possível obter Nome de Usuário.");
        }

        if(!GroupService.getInstance().emailAvailableForOrganization(email)) {
            throw new UserException("Email \""+email+"\" não esta disponível para cadastro!");
        }

        User user = findByEmail(email).orElse( null );

        if ( user == null ) {
            // register user
            if(!usernameExist(username.trim())) {

                user = new User();
                user.setName(username.trim());
                user.setEmail(email.trim());
                createUser(user, null, null);

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
                    profile.setImage( ImageMetadataService.getInstance().saveExternalImage( pictureUrl.trim(), profile, false ) );
                }

                profileRepository.saveAndFlush(profile);

                saveInSession("novoUsuario", true);

            } else {
                throw new UserException("Usúario \""+username+"\" já existe.");
            }
        }

        if(user != null) {

            // enable verified seal on account
            if(!user.isEmail_verified()) {
                user.setEmail_verified(true);
                save(user);
            }

            saveInSession("loginViaGoogle", true);

            configureSessionForUser(user, Sys.context.getBean("authenticationManager", AuthenticationManager.class));

            return user;
        }

        return null;
    }

    public String getPublicUrl() {
        try {
            if(PUBLIC_URL != null && !PUBLIC_URL.isEmpty()) {
                return PUBLIC_URL;
            }
            URL requestUrl = new URL(getRequest().getRequestURL().toString());
            String port = requestUrl.getPort() > 0 && requestUrl.getPort() != 80 && requestUrl.getPort() != 443
                ? ":" + requestUrl.getPort()
                : "";

            return requestUrl.getProtocol() + "://" + requestUrl.getHost() + port + contextPath;
        } catch (Exception e) {
            return null;
        }
    }

    public String getRefererUrlBase() {
        try {
            String refererHeader = getRequest().getHeader("Referer");
            if(refererHeader != null && !refererHeader.isEmpty()) {
                URL requestUrl = new URL(refererHeader);
                String port = requestUrl.getPort() > 0 && requestUrl.getPort() != 80 && requestUrl.getPort() != 443
                        ? ":" + requestUrl.getPort()
                        : "";
                return requestUrl.getProtocol() + "://" + requestUrl.getHost() + port;
            }
            return getPublicUrl();
        } catch (Exception e) {
            return null;
        }
    }

    public String keycloakLoginUrl() {
        return  getKeycloakUrl() + "/realms/" + getKeycloakRealm() + "/protocol/openid-connect/auth?client_id=" + getKeycloakClientId() + "&redirect_uri="+ getKeycloakRedirectUrl() +"&response_type=code";
    }

    public String urlDefaultKeycloakRedirectCallback() {
        return getRefererUrlBase() + "/keycloak-oauth-redirect";
    }

    public String getKeycloakRedirectUrl() {
        GroupEnvironment envG = GroupService.getInstance().getOrganizationEnvironment();
        if(envG != null && envG.keycloak_redirect_url != null && !envG.keycloak_redirect_url.isEmpty()) {
            return envG.keycloak_redirect_url;
        }
        if(KEYCLOAK_REDIRECT_URL != null && !KEYCLOAK_REDIRECT_URL.isEmpty()){
            return KEYCLOAK_REDIRECT_URL;
        }
        return urlDefaultKeycloakRedirectCallback();
    }

    public String getKeycloakClientId() {
        GroupEnvironment envG = GroupService.getInstance().getOrganizationEnvironment();
        if(envG != null && envG.keycloak_client_id != null && !envG.keycloak_client_id.isEmpty()) {
            return envG.keycloak_client_id;
        }
        return KEYCLOAK_CLIENT_ID;
    }

    public String getKeycloakClientSecret() {
        GroupEnvironment envG = GroupService.getInstance().getOrganizationEnvironment();
        if(envG != null && envG.keycloak_client_secret != null && !envG.keycloak_client_secret.isEmpty()) {
            return envG.keycloak_client_secret;
        }
        return KEYCLOAK_CLIENT_SECRET;
    }

    public String getKeycloakRealm() {
        GroupEnvironment envG = GroupService.getInstance().getOrganizationEnvironment();
        if(envG != null && envG.keycloak_realm != null && !envG.keycloak_realm.isEmpty()) {
            return envG.keycloak_realm;
        }
        return KEYCLOAK_REALM;
    }

    public String getKeycloakUrl() {
        GroupEnvironment envG = GroupService.getInstance().getOrganizationEnvironment();
        if(envG != null && envG.keycloak_url != null && !envG.keycloak_url.isEmpty()) {
            return envG.keycloak_url.replaceAll("/$", "");
        }
        return KEYCLOAK_URL;
    }

    public boolean isKeycloakEnabled() {
        GroupEnvironment envG = GroupService.getInstance().getOrganizationEnvironment();
        if(envG != null) {
            return envG.keycloak_enabled;
        }
        return KEYCLOAK_ENABLED;
    }

    public void setupEmailSender() {
        GroupEnvironment envG = GroupService.getInstance().getOrganizationEnvironment();
        if(envG != null && envG.email_enabled) {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost(envG.email_host);
            mailSender.setPort(Integer.parseInt(envG.email_port == null ? "587" : envG.email_port));
            mailSender.setUsername(envG.email_username);
            mailSender.setPassword(envG.email_password);

            Properties props = System.getProperties();
            props.remove("mail.transport.protocol");
            props.remove("mail.smtp.ssl.trust");
            props.remove("mail.smtp.auth");
            props.remove("mail.smtp.starttls.enable");
            props.put("mail.transport.protocol", envG.email_protocol == null ? "smtp" : envG.email_protocol);
            props.put("mail.smtp.ssl.trust", mailSender.getHost());
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            mailSender.setJavaMailProperties(props);

            emailSender = (JavaMailSender) mailSender;
        } else {
            emailSender = null;
        }
    }

    public JavaMailSender getEmailSender() {
        if(emailSender == null) {
            setupEmailSender();
        }
        return emailSender;
    }

    public GetAccountDTO getAccountSession() {
        GetAccountDTO getAccount = null;
        if(userIsLoggedIn()) {
            getAccount = new GetAccountDTO(getUserInSession(), RoleService.getInstance().getAllRolesSession());
        }
        if(getInSession("account_confirmed") != null) {
            removeInSession("account_confirmed");
            removeInSession("message_account_confirmed");
        }
        return getAccount;
    }

    public boolean logoutUserSession() {
        if(userIsLoggedIn()) {
            logout();
            return true;
        }
        return false;
    }

    public GetAvailableCheckDTO availableUsernameCheck(String username) {
        boolean usernameRegex = usernameRegex(username);
        boolean usernameExist = usernameExist(username);

        return new GetAvailableCheckDTO(
                usernameRegex && !usernameExist,
                !usernameRegex ? "Verifique o formato do nome de usuário." : usernameExist ? "Este nome de usuário está em uso." : "Usuário Disponível para uso."
        );
    }

    public GetAvailableCheckDTO availableEmailCheck(String email) {
        boolean emailRegex = emailRegex(email);
        boolean emailExist = emailExist(email);
        boolean emailAvailableForOrganization = GroupService.getInstance().emailAvailableForOrganization(email);

        return new GetAvailableCheckDTO(
                emailRegex && !emailExist && emailAvailableForOrganization,
                !emailRegex ? "Verifique o formato do email." : emailExist ? "Este email já está em uso." : !emailAvailableForOrganization ? "Email não autorizado.\nUtilize seu email corporativo." :  "Email Disponível para uso."
        );
    }

    public boolean createAccount(CreateAccountDTO createAccountDTO) throws UserException {

        // check if register is enabled
        if(!isSignupEnabled()) {
            throw new UserException("Registrar-se está desativado!");
        }

        // check recaptcha if available
        checkRecaptchaWithToken(createAccountDTO.recaptchaToken());

        String username = createAccountDTO.username();
        String email = createAccountDTO.email();

        String firstname = createAccountDTO.firstname();
        String lastname = createAccountDTO.lastname();

        String password = createAccountDTO.password();

        if (username==null || username.isEmpty()) {
            throw new UserException("Verifique o campo Usuário!");
        } else {
            username = username.trim().toLowerCase();
            if(!usernameRegex(username)) {
                throw new UserException("Nome de usuário está com formato inválido!");
            }
        }
        if (email==null || email.isEmpty()) {
            throw new UserException("Verifique o campo Email!");
        } else {
            email = email.trim().toLowerCase();
            if(!emailRegex(email)) {
                throw new UserException("Email está com formato inválido!");
            }
        }
        if (password==null || password.isEmpty()) {
            throw new UserException("Verifique o campo Senha!");
        } else {
            if(!passwordRegex(password)) {
                throw new UserException("Senha está com formato inválido!");
            }
        }

        if(usernameExist(username)) {
            throw new UserException("Usuário \""+username+"\" já esta cadastrado!");
        }
        if(emailExist(email)) {
            throw new UserException("Email \""+email+"\" já esta cadastrado!");
        }
        if(!GroupService.getInstance().emailAvailableForOrganization(email)) {
            throw new UserException("Email \""+email+"\" não esta disponível para cadastro!");
        }

        User user = new User();
        user.setName(username);
        user.setEmail(email);
        if(isConfirmAccountEnabled()) {
            user.setInactive(true);
        }
        saveRawPasswordToUser(user, password, false);

        createUser(user, firstname, lastname);

        if(isConfirmAccountEnabled()) {
            sendConfirmAccountEmail(user, true);
        }

        return true;
    }

    public void editAccount(UpdateAccountDTO updateAccountDTO) {
        String newPassword = updateAccountDTO.newPassword();
        if(newPassword == null || newPassword.isEmpty()) {
            throw new UserException("Parametro newPassword é nulo.");
        }

        if(!passwordRegex(newPassword)) {
            throw new UserException("Nova Senha está com formato inválido!");
        }

        String password = updateAccountDTO.password();

        User user = getUserInSession();

        // if logged with google don't check password
        boolean loggedAsGoogle = (getInSession("loginViaGoogle") != null);

        if (loggedAsGoogle || passwordValid(user, password)) {

            saveRawPasswordToUser(user, newPassword, false);

            updateUserInSession();

        } else {
            throw new UserException("Credenciais Invalidas!");
        }
    }

    public void adminEditAccount(EditAccountDTO editAccountDTO) {
        if(!isUserAdminSession()) {
            throw new UserException("Você não tem permissão para editar usuário.");
        }

        var userId = CastingUtil.getUUID( editAccountDTO.userId()).orElse( null );
        if(userId == null) {
            throw new UserException("Parametro userId é nulo.");
        }

        String username = editAccountDTO.username();
        String email = editAccountDTO.email();
        String password = editAccountDTO.password();
        String authorityLevel = editAccountDTO.authorityLevel();

        Boolean emailVerified = editAccountDTO.emailVerified();
        Boolean blockedAccount = editAccountDTO.blockedAccount();
        Boolean inactiveAccount = editAccountDTO.inactiveAccount();
        Boolean credentialsExpired = editAccountDTO.credentialsExpired();
        Boolean expiredUser = editAccountDTO.expiredUser();

        User userEdit = find(userId).orElse( null );
        if(userEdit == null) {
            throw new UserException("Usuário não encontrado.");
        }

        String usernameOld = userEdit.getUsername();

        if(username != null && !username.isEmpty()) {
            if(usernameExist(username) && !username.equals(usernameOld)) {
                throw new UserException("Usuário \""+username+"\" já esta cadastrado!");
            }
            if(usernameRegex(username)) {
                userEdit.setName(username);
            } else {
                throw new UserException("Nome de Usuário está com formato inválido!");
            }
        }
        if(email != null && !email.isEmpty()) {
            if(emailExist(email) && !email.equals(userEdit.getEmail())) {
                throw new UserException("Email \""+email+"\" já esta cadastrado!");
            }
            if(emailRegex(email)) {
                userEdit.setEmail(email);
            } else {
                throw new UserException("Email está com formato inválido!");
            }
        }
        if(password != null && !password.isEmpty()) {
            saveRawPasswordToUser(userEdit, password, false);
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

        save(userEdit);

        // force logout
        logoutUsername(usernameOld);
    }

    public List<User> adminListAccount(String byRole) {
        if(!isUserAdminSession()) {
            throw new UserException("Você não tem permissão para listar usuários.");
        }
        return findAllUsers(byRole);
    }

    public User keycloackLogin( LoginCodeDTO loginCodeDTO) {
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

            String bodyJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            Map<String, Object> decodedToken = new ObjectMapper().readValue(bodyJson, Map.class);

            String email = (String) decodedToken.get("email");
            String username = (String) decodedToken.get("preferred_username");
            String name = (String) decodedToken.get("name");
            String pictureUrl = (String) decodedToken.get("picture");

            User user = configureLoginForOAuth(name, username, email, pictureUrl);

            if (user != null) {
                return user;
            }
        } catch (Exception e) {
            if(e.getClass() == UserException.class) {
                throw (UserException) e;
            }
        }

        throw new UserException("Falha ao fazer login com Keycloak.");
    }

    public User googleLogin( LoginTokenDTO loginTokenDTO ) {
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

            User user = configureLoginForOAuth(name, username, email, pictureUrl);

            if(user != null) {
                return user;
            }

        } else {
            throw new UserException("Token de Autenticação é Inválida.");
        }

        throw new UserException("Falha ao fazer login com Google.");
    }

    public URI getKeycloakLoginUrl() {
        if(!isKeycloakEnabled()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "denied access to keycloak login");
        }
        return URI.create(keycloakLoginUrl());
    }

    public void recoveryPassword( RecoveryPasswordDTO recoveryPasswordDTO ) {

        checkRecaptchaWithToken(recoveryPasswordDTO.recaptchaToken());

        String usernameOrEmail = recoveryPasswordDTO.username();

        if(usernameOrEmail == null) {
            throw new UserException("Parametro username é nulo.");
        }

        User user = null;

        try {
            user = (User) loadUserByUsername(usernameOrEmail);
        } catch (Exception e) {
            throw new UserException("Conta não encontrada!");
        }

        sendRecoveryPasswordEmail(user);
    }

    public void recoveryNewPassword( RecoveryNewPasswordDTO recoveryNewPasswordDTO ) {
        String token = recoveryNewPasswordDTO.token();
        String newPassword = recoveryNewPasswordDTO.newPassword();

        if(token == null) {
            throw new UserException("Parametro token é nulo.");
        }
        if(newPassword == null) {
            throw new UserException("Parametro newPassword é nulo.");
        }

        if(!passwordRegex(newPassword)) {
            throw new UserException("Nova Senha está com formato inválido!");
        }

        User user = getUserByRecoveryPasswordToken(token);

        if(user == null) {
            throw new UserException("Token de recuperação de senha inválido ou expirado!");
        }

        user.setRecoveryPasswordToken(null);
        user.setInactive(false);
        saveRawPasswordToUser(user, newPassword, true);
    }

    public void requestConfirmAccountEmail() {
        User user = getUserInSession();
        if(isAccountConfirmed(user)) {
            throw new UserException("Conta já confirmada!");
        }
        sendConfirmAccountEmail(user, false);
    }

    public Boolean confirmAccountEmail(String token) throws RuntimeException {
        User user = token==null ? null : getUserByRecoveryPasswordToken(token);
        if(user == null) {
            return false;
        }

        user.setRecoveryPasswordToken(null);
        user.setInactive(false);
        user.setConfirmed(true);
        save(user);

        saveInSession("account_confirmed", true);
        return true;
    }

    @Override
    public boolean hasPermissionToEdit( User user ) {
        return isSessionOfUser( user );
    }

    @Override
    public boolean hasPermissionToDelete( User user ) {
        return hasPermissionToEdit( user ) || isUserAdminSession();
    }
}
