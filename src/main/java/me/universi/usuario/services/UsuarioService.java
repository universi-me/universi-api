package me.universi.usuario.services;

import me.universi.perfil.entities.Perfil;
import me.universi.perfil.services.PerfilService;
import me.universi.usuario.entities.Usuario;
import me.universi.usuario.enums.Autoridade;
import me.universi.usuario.exceptions.UsuarioException;
import me.universi.usuario.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
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

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> usuario = userRepository.findFirstByNome(username);
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
        Optional<Usuario> usuario = userRepository.findFirstByEmail(email);
        if (usuario.isPresent()) {
            return usuario.get();
        }
        throw new UsuarioException("Email de Usuário não encontrado!");
    }

    public UserDetails findFirstById(Long id) {
        Optional<Usuario> usuario = userRepository.findFirstById(id);
        if (usuario.isPresent()) {
            return usuario.get();
        }
        return null;
    }

    public void createUser(Usuario user) throws UsuarioException {
        if (user==null) {
            throw new UsuarioException("Usuario está vazio!");
        }
        user.setAutoridade(Autoridade.ROLE_USER);
        userRepository.saveAndFlush((Usuario)user);

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

    public boolean senhaValida(Usuario usuario, String senha) {
        return passwordEncoder.matches(senha, usuario.getPassword());
    }

    public void save(Usuario usuario) {
        userRepository.saveAndFlush(usuario);
    }

    public boolean usuarioDonoDaSessao(Usuario usuario) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        if(session != null && usuario != null) {
            Usuario usuarioSession = (Usuario) session.getAttribute("usuario");
            if(usuarioSession != null) {
                return (usuarioSession.getId() == usuario.getId());
            }
        }
        return false;
    }

    public HttpSession obterSessaoAtual() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(true);
    }

    public void atualizarUsuarioNaSessao() {
        HttpSession session = obterSessaoAtual();
        if(session != null) {
            Usuario usuarioSession = (Usuario) session.getAttribute("usuario");
            if(usuarioSession != null) {
                Usuario usuarioAtualizado = (Usuario) findFirstById(usuarioSession.getId());
                if(usuarioAtualizado != null) {
                    session.setAttribute("usuario", usuarioAtualizado);
                }
            }
        }
    }

    public void configurarSessaoParaUsuario(Usuario usuario) {
        HttpSession session = obterSessaoAtual();
        // Set session inatividade do usuario em 10min
        session.setMaxInactiveInterval(10 * 60);

        // Salvar usuario na sessao
        session.setAttribute("usuario", usuario);
    }

    public boolean isContaAdmin(Usuario usuarioSession) {
        try {
            return (usuarioSession.getAutoridade() == Autoridade.ROLE_ADMIN);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean usuarioPrecisaDePerfil(Usuario usuario) {
        try {
            if((usuario.getPerfil()==null || usuario.getPerfil().getNome()==null) && !isContaAdmin(usuario)) {
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

    public String obterUrlAoLogar() {
        HttpSession session = obterSessaoAtual();

        if(session != null) {
            Usuario usuarioSession = (Usuario) session.getAttribute("usuario");
            if (usuarioSession != null) {
                if(usuarioPrecisaDePerfil(usuarioSession)) {
                    return "/p/" + usuarioSession.getUsername() + "/editar";
                } else {
                    // ao logar mandar para o seu perfil
                    return "/p/" + usuarioSession.getUsername();
                }
            }
        }

        SavedRequest lastRequestSaved = (SavedRequest)session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        if(lastRequestSaved != null) {
            // retornar para ultima pagina que usuario clicou
            return lastRequestSaved.getRedirectUrl();
        }

        return "/";
    }

}