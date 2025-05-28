package me.universi.user.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;
import me.universi.Sys;
import me.universi.group.services.OrganizationService;
import me.universi.image.services.ImageMetadataService;
import me.universi.profile.entities.Profile;
import me.universi.profile.repositories.PerfilRepository;
import me.universi.user.entities.User;
import me.universi.user.exceptions.ExceptionResponse;
import me.universi.user.exceptions.UserException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class LoginService {

    private final RequestService requestService;
    private final SessionRegistry sessionRegistry;
    private final PerfilRepository profileRepository;

    public LoginService(RequestService requestService, SessionRegistry sessionRegistry, PerfilRepository profileRepository) {
        this.requestService = requestService;
        this.sessionRegistry = sessionRegistry;
        this.profileRepository = profileRepository;
    }

    // bean instance via context
    public static LoginService getInstance() {
        return Sys.context.getBean("loginService", LoginService.class);
    }

    public User configureLoginForOAuth(String name, String username, String email, String pictureUrl) throws UserException {
        if(email == null) {
            throw new UserException("Não foi possível obter Email.");
        }
        if(username == null) {
            throw new UserException("Não foi possível obter Nome de Usuário.");
        }

        if(!OrganizationService.getInstance().isEmailAvailable(email)) {
            throw new UserException("Email \""+email+"\" não esta disponível para cadastro!");
        }

        User user = UserService.getInstance().findByEmail(email).orElse( null );

        if ( user == null ) {
            // register user
            if(!UserService.getInstance().usernameExist(username.trim())) {

                user = new User();
                user.setName(username.trim());
                user.setEmail(email.trim());
                UserService.getInstance().createUser(user, null, null, null);

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
                UserService.getInstance().save(user);
            }

            saveInSession("loginViaGoogle", true);

            configureSessionForUser(user, Sys.context.getBean("authenticationManager", AuthenticationManager.class));

            return user;
        }

        return null;
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

    public String getUrlWhenLogout() {
        return "/login";
    }

    public String manageProfilePath() {
        return "/manage-profile";
    }

    // url redirect when user login
    public String getUrlWhenLogin() {

        User userSession = getUserInSession();
        if (userSession != null) {
            if(UserService.getInstance().userNeedAnProfile(userSession, true)) {
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


    // logout user from current session
    public void logout() {
        try {
            SecurityContextHolder.clearContext();
            HttpSession session = requestService.getRequest().getSession(true);
            session.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isSessionOfUser(User user) {
        try {
            return (user.getUsername() != null && Objects.equals(getUserInSession().getUsername(), user.getUsername()));
        } catch (Exception e) {
            return false;
        }
    }



    public HttpSession getActiveSession() {
        HttpSession session = requestService.getRequest().getSession(true);
        Object domain = session.getAttribute("domain");
        if (domain != null) {
            if(!Objects.equals(domain, requestService.getDomainFromRequest())) {
                // force logout if session is invalid, due to not same domain
                throwErrorSessionInvalidAndRedirectToLogin();
            }
        }
        return session;
    }



    public void updateUserInSession() {
        User userSession = getUserInSession();
        if(userSession != null) {
            User actualUser = UserService.getInstance().find(userSession.getId()).orElse(null);
            if(actualUser != null) {
                configureSessionForUser(actualUser, null);
            }
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
        saveInSession("domain", requestService.getDomainFromRequest());

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

    public boolean logoutUserSession() {
        if(userIsLoggedIn()) {
            logout();
            return true;
        }
        return false;
    }


}