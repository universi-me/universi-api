package me.universi.user.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import me.universi.Sys;
import me.universi.group.services.GroupService;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.user.entities.User;
import me.universi.user.enums.Authority;
import me.universi.user.exceptions.UserException;
import me.universi.user.repositories.UserRepository;
import me.universi.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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
    private final Environment env;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ProfileService profileService, RoleHierarchyImpl roleHierarchy, SessionRegistry sessionRegistry, JavaMailSender emailSender, Environment env) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileService = profileService;
        this.roleHierarchy = roleHierarchy;
        this.sessionRegistry = sessionRegistry;
        this.emailSender = emailSender;
        this.env = env;
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
        Optional<User> user = userRepository.findFirstByEmail(email);
        if (user.isPresent()) {
            return user.get();
        }
        throw new UserException("Email de Usuário não encontrado!");
    }

    public UserDetails findFirstByUsername(String username) {
        Optional<User> userGet = userRepository.findFirstByName(username);
        return userGet.orElse(null);
    }

    public UserDetails findFirstById(UUID id) {
        Optional<User> userGet = userRepository.findFirstById(id);
        return userGet.orElse(null);
    }

    public UserDetails findFirstById(String id) {
        return findFirstById(UUID.fromString(id));
    }

    public void createUser(User user) throws Exception {
        if (user==null) {
            throw new UserException("Usuario está vazio!");
        } else if (user.getUsername()==null) {
            throw new UserException("username está vazio!");
        }
        user.setAuthority(Authority.ROLE_USER);

        userRepository.saveAndFlush(user);

        if(user.getProfile() == null) {
            Profile userProfile = new Profile();
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

    public void save(User user) {
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
        return getRequest().getSession(true);
    }

    public String getActiveUrl() {
        return  getRequest().getRequestURI();
    }

    public void updateUserInSession() {
        User userSession = getUserInSession();
        if(userSession != null) {
            User userAtualizado = (User) findFirstById(userSession.getId());
            if(userAtualizado != null) {
                configureSessionForUser(userAtualizado, null);
            }
        }
    }

    public User getUserInSession() {
        HttpSession session = getActiveSession();
        if(session != null) {
            return (User) session.getAttribute("usuario");
        }
        return null;
    }

    public void configureSessionForUser(User user, AuthenticationManager authenticationManager) {
        HttpSession session = getActiveSession();
        // set user session inactivity to 6 months
        session.setMaxInactiveInterval(6 * 30 * 24 * 60 * 60);

        // save user in session
        session.setAttribute("usuario", user);

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

    // url redirect when user login
    public String getUrlWhenLogin() {

        User userSession = getUserInSession();
        if (userSession != null) {
            if(userNeedAnProfile(userSession, true)) {
                // go to user profile edit
                return "/manage-profile";
            } else {
                try {
                    return "/group/" + GroupService.getInstance().getOrganizationBasedInDomain().getNickname();
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
            HttpSession session = getActiveSession();
            session.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUrlWhenLogout() {
        return "/login";
    }

    public void setRawPasswordToUser(User user, String rawPassword, boolean logout) throws UserException {
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
    public void sendConfirmAccountEmail(User user) throws Exception {
        String userIp = getRequest().getHeader("X-Forwarded-For");
        URL requestUrl = new URL(getRequest().getRequestURL().toString());

        String token = generateRecoveryPasswordToken(user);

        String url = "https://" + requestUrl.getHost() + "/api/confirm-account/" + token;
        String subject = "Universi.me - Confirmação de Conta";
        String text = "Olá " + user.getUsername() + ",\n\n" +
                "Seja bem-vindo(a) ao Universi.me, para continuar com o seu cadastro precisamos confirmar a sua conta do Universi.me.\n\n" +
                "Para confirmar sua conta, clique no link abaixo:\n\n" +
                url + "\n\n" +
                "Se você não solicitou a confirmação de conta, por favor, ignore este email.\n\n" +
                "Endereço IP: " + userIp + "\n\n" +
                "Atenciosamente,\n" +
                "Equipe Universi.me";

        sendSystemEmailToUser(user, subject, text);
    }

    // is account confirmed
    public boolean isAccountConfirmed(User user) {
        return !user.isInactive();
    }

    public boolean confirmAccountEnabled() {
        return Boolean.parseBoolean(env.getProperty("SIGNUP_CONFIRMATION_ENABLED"));
    }
}