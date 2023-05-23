package me.universi.exercise.services;

import me.universi.exercise.ExerciseRepository;
import me.universi.exercise.entities.Exercise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListExerciseServiceServiceImpl implements ListExerciseService {
    private final ExerciseRepository exerciseRepository;

    @Autowired
    public ListExerciseServiceServiceImpl(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public List<Exercise> listExercise(Long groupId) {
        return this.exerciseRepository.findAllByGroupIdAndInactivateIsFalse(groupId);
    }
}
