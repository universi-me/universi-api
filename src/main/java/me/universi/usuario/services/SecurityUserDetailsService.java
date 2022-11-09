package me.universi.usuario.services;

import me.universi.usuario.entities.Usuario;
import me.universi.usuario.enums.Autoridade;
import me.universi.usuario.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class SecurityUserDetailsService implements UserDetailsService {
    @Autowired
    private UsuarioRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> usuario = userRepository.findByNome(username);
        if (usuario.isPresent()) {
            return usuario.get();
        }
        throw new UsernameNotFoundException("Usuário não encontrado!");
    }

    public void createUser(Usuario user) throws Exception {
        if (user==null) {
            throw new Exception("Usuario está vazio!");
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

}