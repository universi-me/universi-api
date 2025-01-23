package me.universi.education.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.universi.education.entities.EducationType;

import java.util.UUID;

@Repository
public interface EducationTypeRepository extends JpaRepository<EducationType, UUID> {
    Optional<EducationType> findFirstByNameIgnoreCase( String name );
    Optional<EducationType> findFirstByIdOrNameIgnoreCase( UUID id, String name );
}

