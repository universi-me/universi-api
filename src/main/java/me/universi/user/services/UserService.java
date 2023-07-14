package me.universi.user.services;

import jakarta.servlet.http.HttpServletRequest;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.PerfilService;
import me.universi.user.entities.User;
import me.universi.user.enums.Authority;
import me.universi.user.exceptions.UserException;
import me.universi.user.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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

import jakarta.servlet.http.HttpSession;

import java.util.Collection;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final PerfilService perfilService;

    private final RoleHierarchyImpl roleHierarchy;

    private final SessionRegistry sessionRegistry;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, PerfilService perfilService, RoleHierarchyImpl roleHierarchy, SessionRegistry sessionRegistry) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.perfilService = perfilService;
        this.roleHierarchy = roleHierarchy;
        this.sessionRegistry = sessionRegistry;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> usuario = userRepository.findFirstByName(username);
        if (usuario.isPresent()) {
            return usuario.get();
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
        Optional<User> usuario = userRepository.findFirstByEmail(email);
        if (usuario.isPresent()) {
            return usuario.get();
        }
        throw new UserException("Email de Usuário não encontrado!");
    }

    public UserDetails findFirstById(Long id) {
        Optional<User> usuario = userRepository.findFirstById(id);
        if (usuario.isPresent()) {
            return usuario.get();
        }
        return null;
    }

    public void createUser(User user) throws UserException {
        if (user==null) {
            throw new UserException("Usuario está vazio!");
        }
        user.setAuthority(Authority.ROLE_USER);
        userRepository.saveAndFlush((User)user);

        Profile userProfile = new Profile();
        userProfile.setUsuario(user);
        perfilService.save(userProfile);
        user.setProfile(userProfile);
    }

    public long count() {
        try {
            return userRepository.count();
        } catch (Exception e) {
            return 0;
        }
    }

    public String encodePassword(String senha) {
        return passwordEncoder.encode(senha);
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
        String usuarioRegex = "^[a-z0-9_-]+$";
        Pattern emailPattern = Pattern.compile(usuarioRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPattern.matcher(username);
        return matcher.find();
    }

    public boolean emailRegex(String email) {
        String emailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern emailPattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPattern.matcher(email);
        return matcher.find();
    }

    public boolean passwordValid(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public void save(User user) {
        userRepository.saveAndFlush(user);
    }

    public boolean isSessionOfUser(User user) {
        User userSession = getUserInSession();
        if(userSession != null) {
            return (userSession.getId() == user.getId());
        }
        return false;
    }

    public HttpSession getActiveSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(true);
    }

    public String getActiveUrl() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getRequestURI();
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
            User userSession = (User) session.getAttribute("usuario");
            if(userSession != null) {
                return userSession;
            }
        }
        return null;
    }

    public void configureSessionForUser(User user, AuthenticationManager authenticationManager) {
        HttpSession session = getActiveSession();
        // set user session timeout to 10min
        session.setMaxInactiveInterval(10 * 60);

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
        if (reachableRoles.contains(new SimpleGrantedAuthority(authority.toString()))) {
            return true;
        }
        return false;
    }

    public boolean isUserAdmin(User userSession) {
        try {
            return userHasAuthority(userSession, Authority.ROLE_ADMIN);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean userNeedAnProfile(User user) {
        try {
            if((user.getProfile()==null || user.getProfile().getFirstname()==null) && !isUserAdmin(user)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public String getLastSpringSecurityError(Exception exception) {
        String error = null;
        if (exception instanceof BadCredentialsException) {
            error = "Credenciais Invalidas!";
        } else if(exception != null) {
            error = exception.getLocalizedMessage();
        }
        return error;
    }

    // url redirect when user login
    public String getUrlWhenLogin() {

        User userSession = getUserInSession();
        if (userSession != null) {
            if(userNeedAnProfile(userSession)) {
                // go to user profile edit
                return "/p/" + userSession.getUsername() + "/editar";
            } else {
                // go to user profile
                return "/p/" + userSession.getUsername();
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
                    for(SessionInformation sInfo : sessionRegistry.getAllSessions(principal, false)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}