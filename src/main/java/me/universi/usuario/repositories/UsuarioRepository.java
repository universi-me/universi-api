package me.universi.usuario.repositories;

import me.universi.usuario.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findFirstByEmail(String email);
    Optional<Usuario> findFirstByNome(String nome);
    Optional<Usuario> findFirstById(Long id);
}
