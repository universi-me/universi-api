package me.universi.curriculum.profileExperience.repositories;

import me.universi.curriculum.profileExperience.entities.ProfileExperience;
import me.universi.profile.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileExperienceRepository extends JpaRepository<ProfileExperience, UUID> {

    public List<ProfileExperience> findByProfile(Profile profile);
}

