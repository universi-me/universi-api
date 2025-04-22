package me.universi.profile.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import me.universi.profile.entities.Department;

public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    Optional<Department> findFirstByNameIgnoreCaseOrAcronymIgnoreCase( String name, String acronym );
    Optional<Department> findFirstByIdOrNameIgnoreCaseOrAcronymIgnoreCase( UUID id, String name, String acronym );
}
