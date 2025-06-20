package me.universi.education.repositories;

import java.util.Optional;

import me.universi.education.entities.Education;
import me.universi.profile.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.security.interfaces.EdECKey;
import java.util.List;
import java.util.UUID;

@Repository
public interface EducationRepository extends JpaRepository<Education, UUID> {

    Optional<Education> findFirstById(UUID id);
}

