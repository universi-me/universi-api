package me.universi.institution.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import me.universi.institution.entities.Institution;

public interface InstitutionRepository extends JpaRepository<Institution, UUID> {
}
