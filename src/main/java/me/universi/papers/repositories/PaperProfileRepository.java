package me.universi.papers.repositories;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import me.universi.group.entities.Group;
import me.universi.papers.entities.Paper;
import me.universi.papers.entities.PaperFeature;
import me.universi.papers.entities.PaperProfile;
import me.universi.profile.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaperProfileRepository extends JpaRepository<PaperProfile, UUID> {
    Optional<PaperProfile> findFirstById(UUID uuid);
    Optional<PaperProfile> findFirstByPaperAndProfile(Paper paper, Profile feature);

    Optional<PaperProfile> findFirstByProfileAndGroup(Profile profile, Group group);

    Collection<PaperProfile> findAllByGroup(Group group);

    Optional<PaperProfile> findFirstByPaperAndProfileAndGroup(Paper paper, Profile profile, Group group);
}
