package me.universi.activity.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.validation.constraints.NotNull;
import me.universi.activity.entities.*;
import me.universi.profile.entities.Profile;

@Repository
public interface ActivityParticipantRepository extends JpaRepository<ActivityParticipant, UUID> {
    Optional<ActivityParticipant> findFirstByActivityAndProfile( @NotNull Activity activity, @NotNull Profile profile );
    List<ActivityParticipant> findByActivity( @NotNull Activity activity );
}
