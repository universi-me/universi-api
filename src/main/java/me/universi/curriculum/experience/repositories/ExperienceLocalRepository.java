package me.universi.curriculum.experience.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import me.universi.curriculum.experience.entities.ExperienceLocal;

public interface ExperienceLocalRepository extends JpaRepository<ExperienceLocal, UUID> {
}
