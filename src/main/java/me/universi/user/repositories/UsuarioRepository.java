package me.universi.user.repositories;

import me.universi.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UsuarioRepository extends JpaRepository<User, Long> {
    Optional<User> findFirstByEmail(String email);
    Optional<User> findFirstByNome(String nome);
    Optional<User> findFirstById(Long id);
}