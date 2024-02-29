package me.universi.papers.repositories;

import java.util.Optional;
import java.util.UUID;
import me.universi.papers.entities.Paper;
import me.universi.papers.entities.PaperFeature;
import me.universi.papers.entities.PaperProfile;
import me.universi.papers.enums.FeaturesTypes;
import me.universi.profile.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaperFeatureRepository extends JpaRepository<PaperFeature, UUID> {
    Optional<PaperFeature> findFirstById(UUID uuid);
    Optional<PaperFeature> findFirstByPaperAndFeatureType(Paper paper, FeaturesTypes featuresType);
}
