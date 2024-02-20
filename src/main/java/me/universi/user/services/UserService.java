package me.universi.user.services;

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
    private final JavaMailSender emailSender;
    private final Executor emailExecutor;

    public String BUILD_HASH = "development";

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

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ProfileService profileService, RoleHierarchyImpl roleHierarchy, SessionRegistry sessionRegistry, JavaMailSender emailSender) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileService = profileService;
        this.roleHierarchy = roleHierarchy;
        this.sessionRegistry = sessionRegistry;
        this.emailSender = emailSender;
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
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
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

    public boolean isUserAdmin(User userSession) {
        try {
            return userHasAuthority(userSession, Authority.ROLE_ADMIN);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isUserAdminSession() {
        return isUserAdmin(getUserInSession());
    }

    public boolean userNeedAnProfile(User user, boolean checkAdmin) {
        try {
            if(checkAdmin && isUserAdmin(user)) {
                return false;
            }
            return (user.getProfile() == null || user.getProfile().getFirstname() == null);
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

    public void sendSystemEmailToUser(UserDetails user, String subject, String text) throws UserException {
        String email = ((User) user).getEmail();
        if (email == null) {
            throw new UserException("Usuário não possui um email.");
        }
        emailExecutor.execute(() -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(((User) user).getEmail());
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);
        });
    }

    // generate recovery password token sha256 for user
    public String generateRecoveryPasswordToken(User user) throws Exception {
        String tokenRandom = UUID.randomUUID().toString();

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(tokenRandom.getBytes(StandardCharsets.UTF_8));
        String tokenString = ConvertUtil.bytesToHex(encodedHash);

        user.setRecoveryPasswordToken(tokenString);
        save(user);

        return tokenString;
    }

    // send recovery password email to user
    public void sendRecoveryPasswordEmail(User user) throws Exception {
        String userIp = getRequest().getHeader("X-Forwarded-For");
        URL requestUrl = new URL(getRequest().getRequestURL().toString());

        String token = generateRecoveryPasswordToken(user);

        String url = "https://" + requestUrl.getHost() + "/recovery-password/" + token;
        String subject = "Universi.me - Recuperação de Senha";
        String text = "Olá " + user.getUsername() + ",\n\n" +
                "Você solicitou a recuperação de senha para sua conta no Universi.me.\n" +
                "Para recuperar sua senha, clique no link abaixo:\n\n" +
                url + "\n\n" +
                "Se você não solicitou a recuperação de senha, por favor, ignore este email.\n\n" +
                "Endereço IP: " + userIp + "\n\n" +
                "Atenciosamente,\n" +
                "Equipe Universi.me";
        sendSystemEmailToUser(user, subject, text);
    }

    public User getUserByRecoveryPasswordToken(String token) {
        return userRepository.findFirstByRecoveryPasswordToken(token).orElse(null);
    }

    //send confirmation signup account email to user
    public void sendConfirmAccountEmail(User user, boolean signup) throws Exception {
        String userIp = getRequest().getHeader("X-Forwarded-For");
        URL requestUrl = new URL(getRequest().getRequestURL().toString());

        String token = generateRecoveryPasswordToken(user);

        String url = "https://" + requestUrl.getHost() + "/api/confirm-account/" + token;
        String subject = "Universi.me - Confirmação de Conta";
        String messageExplain = (signup) ? "Seja bem-vindo(a) ao Universi.me, para continuar com o seu cadastro precisamos confirmar a sua conta do Universi.me." : "Você solicitou a confirmação de sua conta no Universi.me.";
        String text = "Olá " + user.getUsername() + ",\n\n" +
                messageExplain + "\n\n" +
                "Para confirmar sua conta, clique no link abaixo:\n\n" +
                url + "\n\n" +
                "Se você não solicitou a confirmação de conta, por favor, ignore este email.\n\n" +
                "Endereço IP: " + userIp + "\n\n" +
                "Atenciosamente,\n" +
                "Equipe Universi.me";

        sendSystemEmailToUser(user, subject, text);
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
                isTokenValid = (((Double)((Map)responseCaptcha.get("riskAnalysis")).get("score")) >= 0.5);
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
        return BUILD_HASH;
    }
}