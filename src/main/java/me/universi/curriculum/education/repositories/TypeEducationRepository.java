package me.universi.curriculum.education.repositories;

import me.universi.curriculum.education.entities.TypeEducation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TypeEducationRepository extends JpaRepository<TypeEducation, UUID> {
}

