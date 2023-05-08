package me.universi.exercise;

import me.universi.exercise.entities.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    Optional<Exercise> findByIdAndGroupId(Long exerciseId, Long groupId);

    Boolean existsByIdAndGroupAdminId(Long exerciseId, Long adminId);
}
