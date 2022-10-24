package me.universi.seguranca;

import me.universi.usuario.entities.Usuario;
import me.universi.usuario.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SecurityUserDetailsService implements UserDetailsService
{
    @Autowired
    private UsuarioRepository userRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        Optional<Usuario> usuario = userRepository.findByEmail(username);
        if (usuario.isPresent()) {
            Usuario user = usuario.get();
            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            UserDetails userDetails =  new org.springframework.security.core.userdetails.User(user.getEmail(),user.getSenha(), true,true, true,true,authorities);
            return userDetails;
        }
        throw new UsernameNotFoundException("Usuário não encontrado!");
    }

    public void createUser(Usuario user) throws Exception
    {
        if (user==null) {
            throw new Exception("Usuario está vazio!");
        }
        if (user.getEmail()==null || user.getEmail().length()==0 || !user.getEmail().contains("@")) {
            throw new Exception("Verifique o campo Email!");
        }
        if (user.getNome()==null || user.getNome().length()==0) {
            throw new Exception("Verifique o campo Nome!");
        }
        if (user.getSenha()==null || user.getSenha().length()==0) {
            throw new Exception("Verifique o campo Senha!");
        }
        Optional<Usuario> usuario = userRepository.findByEmail(user.getEmail());
        if (usuario.isPresent()) {
            throw new Exception("Usuário com email \""+user.getEmail()+"\" já esta cadastrado!");
        }
        userRepository.save((Usuario) user);
    }
}