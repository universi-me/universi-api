package me.universi.usuario.services;

import me.universi.usuario.entities.Usuario;
import me.universi.usuario.enums.Autoridade;
import me.universi.usuario.exceptions.UsuarioException;
import me.universi.usuario.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpSession;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecurityUserDetailsService implements UserDetailsService {
    @Autowired
    private UsuarioRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

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

    public void createUser(Usuario user) throws UsuarioException {
        if (user==null) {
            throw new UsuarioException("Usuario está vazio!");
        }
        user.setAutoridade(Autoridade.ROLE_USER);
        userRepository.save((Usuario)user);
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
        userRepository.save(usuario);
    }

    public void configurarSessaoParaUsuario(HttpSession session, Usuario usuario) {

        // Set session inatividade do usuario em 10min
        session.setMaxInactiveInterval(10 * 60);

        // Salvar usuario na sessao
        session.setAttribute("usuario", usuario);
    }
}