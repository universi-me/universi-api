package me.universi.profile.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import me.universi.profile.entities.Department;

public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    Optional<Department> findFirstByAcronymIgnoreCase( String acronym );
    Optional<Department> findFirstByIdOrAcronymIgnoreCase( UUID id, String acronym );
}
