package me.universi.exercise.services;

import me.universi.exercise.ExerciseRepository;
import me.universi.exercise.entities.Exercise;
import me.universi.grupo.entities.Group;
import me.universi.grupo.exceptions.GroupNotFoundException;
import me.universi.grupo.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateExerciseServiceImpl implements CreateExerciseService{

    private final GroupRepository groupRepository;
    private final ExerciseRepository exerciseRepository;

    @Autowired
    public CreateExerciseServiceImpl(GroupRepository groupRepository, ExerciseRepository exerciseRepository) {
        this.groupRepository = groupRepository;
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public Exercise createExercise(Long groupId, Exercise exercise) {
        Group group = this.groupRepository.findFirstById(groupId).orElseThrow(GroupNotFoundException::new);

        return this.exerciseRepository.save(exercise);
    }
}
