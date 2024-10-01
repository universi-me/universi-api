package me.universi.user.services;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import me.universi.Sys;
import me.universi.group.entities.Group;
import me.universi.group.entities.GroupSettings.GroupEnvironment;
import me.universi.group.services.GroupService;
import me.universi.profile.entities.Profile;
import me.universi.profile.exceptions.ProfileException;
import me.universi.profile.services.ProfileService;
import me.universi.user.entities.User;
import me.universi.user.enums.Authority;
import me.universi.user.exceptions.ExceptionResponse;
import me.universi.user.exceptions.UserException;
import me.universi.user.repositories.UserRepository;
import me.universi.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileService profileService;
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

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ProfileService profileService, RoleHierarchyImpl roleHierarchy, SessionRegistry sessionRegistry) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileService = profileService;
        this.roleHierarchy = roleHierarchy;
        this.sessionRegistry = sessionRegistry;
        this.emailExecutor = Executors.newFixedThreadPool(5);
    }

    // UserService bean instance via context
    public static UserService getInstance() {
        return Sys.context.getBean("userService", UserService.class);
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = findFirstByUsername(username);
        if (user != null) {
            return user;
        }
        if(emailRegex(username)) {
            try {
                return findFirstByEmail(username);
            } catch (UserException e) {
                throw new UsernameNotFoundException("Usuário não encontrado!");
            }
        }
        throw new UsernameNotFoundException("Usuário não encontrado!");
    }

    public UserDetails findFirstByEmail(String email) throws UserException {
        Group organization = GroupService.getInstance().obtainOrganizationBasedInDomain();
        Optional<User> user = organization == null ? userRepository.findFirstByEmail(email) : userRepository.findFirstByEmailAndOrganizationId(email, organization.getId());
        if (user.isPresent()) {
            return user.get();
        }
        throw new UserException("Email de Usuário não encontrado!");
    }

    public UserDetails findFirstByUsername(String username) {
        Group organization = GroupService.getInstance().obtainOrganizationBasedInDomain();
        Optional<User> userGet = organization == null ? userRepository.findFirstByName(username) : userRepository.findFirstByNameAndOrganizationId(username, organization.getId());
        return userGet.orElse(null);
    }

    public UserDetails findFirstById(UUID id) {
        Optional<User> userGet = userRepository.findFirstById(id);
        return userGet.orElse(null);
    }

    public UserDetails findFirstById(String id) {
        return findFirstById(UUID.fromString(id));
    }

    public void createUser(User user, String firstname, String lastname) throws Exception {
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
            profileService.save(userProfile);
            try {
                // add organization to user profile
                GroupService groupService = GroupService.getInstance();
                groupService.addParticipantToGroup(groupService.getOrganizationBasedInDomain(), userProfile);
            } catch (Exception ignored) {
            }
            user.setProfile(userProfile);
        }
    }

    public long count() {
        try {
            return userRepository.count();
        } catch (Exception e) {
            return 0;
        }
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean usernameExist(String username) {
        try {
            if(loadUserByUsername(username) != null) {
                return true;
            }
        }catch (UsernameNotFoundException e){
            return false;
        }
        return false;
    }

    public boolean emailExist(String email) {
        try {
            if(findFirstByEmail(email) != null) {
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
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
            User actualUser = (User) findFirstById(userSession.getId());
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
    public boolean userHasAuthority(User user, Authority authority) {
        Collection<? extends GrantedAuthority> reachableRoles = roleHierarchy.getReachableGrantedAuthorities(user.getAuthorities());
        return reachableRoles.contains(new SimpleGrantedAuthority(authority.toString()));
    }

    // check if user has equal authority
    public boolean userHasAuthority(User user, Authority authority, boolean equal) {
        if(equal) {
            return user.getAuthority().equals(authority);
        }
        return userHasAuthority(user, authority);
    }

    public boolean isUserRole(User user, Authority role, boolean equal) {
        try {
            if(equal) {
                return userHasAuthority(user, role, true);
            }
            return userHasAuthority(user, role);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isUserAdmin(User userSession) {
        return isUserRole(userSession, Authority.ROLE_ADMIN, false);
    }

    public boolean isUserDev(User userSession) {
        return isUserRole(userSession, Authority.ROLE_DEV, true);
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
    public String generateRecoveryPasswordToken(User user, boolean useIntervalCheck) throws Exception {

        //check recovery date token if less than 15min
        if(useIntervalCheck && user.getRecoveryPasswordTokenDate() != null) {
            long diff = ConvertUtil.getDateTimeNow().getTime() - user.getRecoveryPasswordTokenDate().getTime();
            if(diff < 900000) {
                throw new UserException("Um email de recuperação de senha já foi enviado para esta conta, por favor tente novamente mais tarde.");
            }
        }

        String tokenRandom = UUID.randomUUID().toString();

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
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
    public void sendRecoveryPasswordEmail(User user) throws Exception {
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
    public void sendConfirmAccountEmail(User user, boolean signup) throws Exception {
        String userIp = getClientIpAddress();

        String token = generateRecoveryPasswordToken(user, false);

        String url = getPublicUrl() + "/api/confirm-account/" + token;
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
            try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                BUILD_HASH = FileCopyUtils.copyToString(reader);
            } catch (IOException ignored) {
            }
        }
        if("development".equals(BUILD_HASH) && BUILD_HASH_ENV != null && !BUILD_HASH_ENV.isEmpty()) {
            return BUILD_HASH_ENV;
        }
        return BUILD_HASH;
    }

    public User configureLoginForOAuth(String name, String username, String email, String pictureUrl) throws Exception {
        if(email == null) {
            throw new UserException("Não foi possível obter Email.");
        }
        if(username == null) {
            throw new UserException("Não foi possível obter Nome de Usuário.");
        }

        if(!GroupService.getInstance().emailAvailableForOrganization(email)) {
            throw new UserException("Email \""+email+"\" não esta disponível para cadastro!");
        }

        User user;

        try {
            user = (User)findFirstByEmail(email);
        } catch (UserException e) {
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
                    profile.setImage(pictureUrl.trim());
                }

                profileService.save(profile);

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
            return requestUrl.getProtocol() + "://" + requestUrl.getHost() + ((requestUrl.getPort() > 0 && requestUrl.getPort() != 80 && requestUrl.getPort() != 443)  ? ":"+requestUrl.getPort() : "");
        } catch (Exception e) {
            return null;
        }
    }

    public String keycloakLoginUrl() {
        return  getKeycloakUrl() + "/realms/" + getKeycloakRealm() + "/protocol/openid-connect/auth?client_id=" + getKeycloakClientId() + "&redirect_uri="+ getKeycloakRedirectUrl() +"&response_type=code";
    }

    public String getKeycloakRedirectUrl() {
        GroupEnvironment envG = GroupService.getInstance().getOrganizationEnvironment();
        if(envG != null && envG.keycloak_redirect_url != null && !envG.keycloak_redirect_url.isEmpty()) {
            return envG.keycloak_redirect_url;
        }
        if(KEYCLOAK_REDIRECT_URL != null && !KEYCLOAK_REDIRECT_URL.isEmpty()){
            return KEYCLOAK_REDIRECT_URL;
        }
        return getPublicUrl() + "/keycloak-oauth-redirect";
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

}