package me.universi.exercise.services;

import me.universi.exercise.ExerciseRepository;
import me.universi.exercise.entities.Exercise;
import me.universi.exercise.exception.ExerciseNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExerciseGetServiceImpl implements ExerciseGetService {

    private final ExerciseRepository exerciseRepository;

    @Autowired
    public ExerciseGetServiceImpl(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public Exercise getExercise(UUID exerciseId, UUID groupId) {
        return this.exerciseRepository.findFirstByIdAndGroupId(exerciseId, groupId).orElseThrow(ExerciseNotFoundException::new);
    }
}
