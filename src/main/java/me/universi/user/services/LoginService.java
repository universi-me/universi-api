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
import me.universi.util.ConvertUtil;
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
    private final PerfilRepository profileRepository;

    public LoginService(RequestService requestService, PerfilRepository profileRepository) {
        this.requestService = requestService;
        this.profileRepository = profileRepository;
    }

    // bean instance via context
    public static LoginService getInstance() {
        return Sys.context().getBean("loginService", LoginService.class);
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

            configureSessionForUser(user, null);

            return user;
        }

        return null;
    }

    // logout user remotely
    public void logoutUser(User user) {
        if (user == null) {
            return;
        }
        user.setVersionDate(ConvertUtil.getDateTimeNow());
        UserService.getInstance().save(user);
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

        return "/";
    }


    // logout user from current session
    public void logout() {
        try {
            logoutUser(getUserInSession());
            SecurityContextHolder.clearContext();
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

    public User getUserInSession() {
        // get current request
        HttpServletRequest request = requestService.getRequest();
        if(request != null) {
            User user = (User) JWTService.getInstance().getUserFromRequest(request);
            if(user != null) {
                return user;
            }
        }
        return null;
    }

    public void throwErrorSessionInvalidAndRedirectToLogin() {
        logout();
        throw new ExceptionResponse("Sessão inválida!", "/login");
    }

    public void configureSessionForUser(User user, AuthenticationManager authenticationManager) {

        authenticationManager = authenticationManager != null ? authenticationManager : Sys.context().getBean("authenticationManager", AuthenticationManager.class);

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
            SecurityContextHolder.setContext(securityContext);
        }
    }

    public boolean userIsLoggedIn() {
        try {
            return getUserInSession() != null;
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