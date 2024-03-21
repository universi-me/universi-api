package me.universi.roles.repositories;

import java.util.Optional;
import java.util.UUID;
import me.universi.roles.entities.Roles;
import me.universi.roles.entities.RolesFeature;
import me.universi.roles.enums.FeaturesTypes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesFeatureRepository extends JpaRepository<RolesFeature, UUID> {
    Optional<RolesFeature> findFirstById(UUID uuid);
    Optional<RolesFeature> findFirstByRolesAndFeatureType(Roles roles, FeaturesTypes featuresType);
}
