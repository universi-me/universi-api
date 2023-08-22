package me.universi.exercise;

import me.universi.exercise.entities.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {
    Optional<Exercise> findFirstByIdAndGroupId(UUID exerciseId, UUID groupId);

    Boolean existsByIdAndGroupAdminId(UUID exerciseId, UUID adminId);

    List<Exercise> findAllByGroupIdAndInactivateIsFalse(UUID groupId);
}
