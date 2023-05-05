package me.universi.exercise.services;

import me.universi.exercise.ExerciseRepository;
import me.universi.exercise.dto.ExerciseCreateDTO;
import me.universi.exercise.entities.Exercise;
import me.universi.grupo.entities.Group;
import me.universi.grupo.exceptions.GroupNotFoundException;
import me.universi.grupo.repositories.GroupRepository;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CreateExerciseServiceImpl implements CreateExerciseService{

    private final GroupRepository groupRepository;
    private final ExerciseRepository exerciseRepository;

    private final UserService userService;

    @Autowired
    public CreateExerciseServiceImpl(GroupRepository groupRepository, ExerciseRepository exerciseRepository, UserService userService) {
        this.groupRepository = groupRepository;
        this.exerciseRepository = exerciseRepository;
        this.userService = userService;
    }

    @Override
    public Exercise createExercise(Long groupId, ExerciseCreateDTO exercise) {
        Long idProfile =  this.userService.obterUsuarioNaSessao().getProfile().getId();
        Group group = this.groupRepository.findByIdAndAdminId(
                groupId,
                idProfile
        ).orElseThrow(GroupNotFoundException::new);

        exercise.setGroup(group);
        return this.exerciseRepository.save(Exercise.from(exercise));
    }
}
