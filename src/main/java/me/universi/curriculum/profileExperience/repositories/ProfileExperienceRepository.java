package me.universi.curriculum.profileExperience.repositories;

import me.universi.curriculum.profileExperience.entities.ProfileExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProfileExperienceRepository extends JpaRepository<ProfileExperience, UUID> {
}

