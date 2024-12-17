package me.universi.education.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.universi.education.entities.TypeEducation;

import java.util.UUID;

@Repository
public interface TypeEducationRepository extends JpaRepository<TypeEducation, UUID> {
    Optional<TypeEducation> findFirstByNameIgnoreCase( String name );
    Optional<TypeEducation> findFirstByIdOrNameIgnoreCase( UUID id, String name );
}

