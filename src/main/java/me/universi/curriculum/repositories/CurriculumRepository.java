package me.universi.curriculum.repositories;

import me.universi.curriculum.entities.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CurriculumRepository extends JpaRepository<Curriculum, UUID> {
    Optional<Curriculum> findFirstById(UUID id);
}
