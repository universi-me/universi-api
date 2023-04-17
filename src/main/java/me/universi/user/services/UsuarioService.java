package me.universi.user.services;

import jakarta.servlet.http.HttpServletRequest;
import me.universi.perfil.entities.Perfil;
import me.universi.perfil.services.PerfilService;
import me.universi.user.entities.User;
import me.universi.user.enums.Autoridade;
import me.universi.user.exceptions.UsuarioException;
import me.universi.user.repositories.UsuarioRepository;
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
public class UsuarioService implements UserDetailsService {
    @Autowired
    private UsuarioRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PerfilService perfilService;
    @Autowired
    private RoleHierarchyImpl roleHierarchy;
    @Autowired
    private SessionRegistry sessionRegistry;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> usuario = userRepository.findFirstByNome(username);
        if (usuario.isPresent()) {
            return usuario.get();
        }
        if(emailRegex(username)) {
            try {
                return findFirstByEmail(username);
            } catch (UsuarioException e) {
                throw new UsernameNotFoundException("Usuário não encontrado!");
            }
        }
        throw new UsernameNotFoundException("Usuário não encontrado!");
    }

    public UserDetails findFirstByEmail(String email) throws UsuarioException {
        Optional<User> usuario = userRepository.findFirstByEmail(email);
        if (usuario.isPresent()) {
            return usuario.get();
        }
        throw new UsuarioException("Email de Usuário não encontrado!");
    }

    public UserDetails findFirstById(Long id) {
        Optional<User> usuario = userRepository.findFirstById(id);
        if (usuario.isPresent()) {
            return usuario.get();
        }
        return null;
    }

    public void createUser(User user) throws UsuarioException {
        if (user==null) {
            throw new UsuarioException("Usuario está vazio!");
        }
        user.setAutoridade(Autoridade.ROLE_USER);
        userRepository.saveAndFlush((User)user);

        Perfil userPerfil = new Perfil();
        userPerfil.setUsuario(user);
        perfilService.save(userPerfil);
        user.setPerfil(userPerfil);
    }

    public long count() {
        try {
            return userRepository.count();
        } catch (Exception e) {
            return 0;
        }
    }

    public String codificarSenha(String senha) {
        return passwordEncoder.encode(senha);
    }

    public boolean usernameExiste(String username) {
        try {
            if(loadUserByUsername(username) != null) {
                return true;
            }
        }catch (UsernameNotFoundException e){
            return false;
        }
        return false;
    }

    public boolean emailExiste(String email) {
        try {
            if(findFirstByEmail(email) != null) {
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }

    public boolean usuarioRegex(String username) {
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

    public boolean senhaValida(User user, String senha) {
        return passwordEncoder.matches(senha, user.getPassword());
    }

    public void save(User user) {
        userRepository.saveAndFlush(user);
    }

    public boolean usuarioDonoDaSessao(User user) {
        User userSession = obterUsuarioNaSessao();
        if(userSession != null) {
            return (userSession.getId() == user.getId());
        }
        return false;
    }

    public HttpSession obterSessaoAtual() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(true);
    }

    public String obterUrlAtual() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getRequestURI();
    }

    public void atualizarUsuarioNaSessao() {
        User userSession = obterUsuarioNaSessao();
        if(userSession != null) {
            User userAtualizado = (User) findFirstById(userSession.getId());
            if(userAtualizado != null) {
                configurarSessaoParaUsuario(userAtualizado, null);
            }
        }
    }

    public User obterUsuarioNaSessao() {
        HttpSession session = obterSessaoAtual();
        if(session != null) {
            User userSession = (User) session.getAttribute("usuario");
            if(userSession != null) {
                return userSession;
            }
        }
        return null;
    }

    public void configurarSessaoParaUsuario(User user, AuthenticationManager authenticationManager) {
        HttpSession session = obterSessaoAtual();
        // Set session inatividade do usuario em 10min
        session.setMaxInactiveInterval(10 * 60);

        // Salvar usuario na sessao
        session.setAttribute("usuario", user);

        // Configurar autenticação
        if(authenticationManager != null) {
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attr.getRequest();
            PreAuthenticatedAuthenticationToken preAuthenticatedAuthenticationToken = new PreAuthenticatedAuthenticationToken(user, user.getUsername(), AuthorityUtils.createAuthorityList(user.getAutoridade().name()));
            preAuthenticatedAuthenticationToken.setDetails(new WebAuthenticationDetails(request));
            preAuthenticatedAuthenticationToken.setAuthenticated(false);
            Authentication authentication = authenticationManager.authenticate(preAuthenticatedAuthenticationToken);
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
        }
    }

    public boolean usuarioEstaLogado() {
        try {
            return SecurityContextHolder.getContext().getAuthentication() != null &&
                    SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                    !(SecurityContextHolder.getContext().getAuthentication()
                            instanceof AnonymousAuthenticationToken);
        } catch (Exception e) {
            return false;
        }
    }

    // verifica se usuario possui a autoridade seguindo a hierarquia do springsecurity
    public boolean usuarioTemAutoridade(User user, Autoridade autoridade) {
        Collection<? extends GrantedAuthority> reachableRoles = roleHierarchy.getReachableGrantedAuthorities(user.getAuthorities());
        if (reachableRoles.contains(new SimpleGrantedAuthority(autoridade.toString()))) {
            return true;
        }
        return false;
    }

    public boolean isContaAdmin(User userSession) {
        try {
            return usuarioTemAutoridade(userSession, Autoridade.ROLE_ADMIN);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean usuarioPrecisaDePerfil(User user) {
        try {
            if((user.getPerfil()==null || user.getPerfil().getNome()==null) && !isContaAdmin(user)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public String erroSpringSecurityMemsagem(Exception exception) {
        String error = null;
        if (exception instanceof BadCredentialsException) {
            error = "Credenciais Invalidas!";
        } else if(exception != null) {
            error = exception.getLocalizedMessage();
        }
        return error;
    }

    // url de redirecionamento ao logar
    public String obterUrlAoLogar() {

        User userSession = obterUsuarioNaSessao();
        if (userSession != null) {
            if(usuarioPrecisaDePerfil(userSession)) {
                return "/p/" + userSession.getUsername() + "/editar";
            } else {
                // ao logar mandar para o seu perfil
                return "/p/" + userSession.getUsername();
            }
        }

        HttpSession session = obterSessaoAtual();
        SavedRequest lastRequestSaved = (SavedRequest)session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        if(lastRequestSaved != null) {
            // retornar para ultima pagina que usuario clicou
            return lastRequestSaved.getRedirectUrl();
        }

        return "/";
    }

    // logout usuario remotamente
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

    // ver se o username tem alguma sassao ativa
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