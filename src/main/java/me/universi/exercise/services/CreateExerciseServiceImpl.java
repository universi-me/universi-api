package me.universi.exercise.services;

import me.universi.exercise.ExerciseRepository;
import me.universi.exercise.dto.ExerciseCreateDTO;
import me.universi.exercise.entities.Exercise;
import me.universi.group.entities.Group;
import me.universi.group.exceptions.GroupNotFoundException;
import me.universi.group.repositories.GroupRepository;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import me.universi.util.ExerciseUtil;

import java.util.UUID;

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
    public Exercise createExercise(UUID groupId, ExerciseCreateDTO exercise) {
        UUID idProfile =  this.userService.getUserInSession().getProfile().getId();
        Group group = this.groupRepository.findFirstByIdAndAdminId(
                groupId,
                idProfile
        ).orElseThrow(GroupNotFoundException::new);
        ExerciseUtil.checkPermissionExercise(this.userService.getUserInSession(), group);

        exercise.setGroup(group);
        return this.exerciseRepository.save(Exercise.from(exercise));
    }
}
