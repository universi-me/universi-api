package me.universi.usuario.repositories;

import me.universi.usuario.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UsuarioRepository extends JpaRepository<User, Long> {
    Optional<User> findFirstByEmail(String email);
    Optional<User> findFirstByNome(String nome);
    Optional<User> findFirstById(Long id);
}
