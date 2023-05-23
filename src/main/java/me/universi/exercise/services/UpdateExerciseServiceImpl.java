package me.universi.exercise.services;

import me.universi.exercise.ExerciseRepository;
import me.universi.exercise.dto.ExerciseUpdateDTO;
import me.universi.exercise.entities.Exercise;
import me.universi.exercise.exception.ExerciseNotFoundException;
import me.universi.group.entities.Group;
import me.universi.group.exceptions.GroupNotFoundException;
import me.universi.group.repositories.GroupRepository;
import me.universi.user.entities.User;
import me.universi.user.exceptions.UnauthorizedException;
import me.universi.user.services.UserService;
import me.universi.util.ExerciseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateExerciseServiceImpl implements UpdateExerciseService{

    private final UserService userService;
    private final GroupRepository groupRepository;
    private final ExerciseRepository exerciseRepository;

    @Autowired
    public UpdateExerciseServiceImpl(UserService userService, GroupRepository groupRepository, ExerciseRepository exerciseRepository) {
        this.userService = userService;
        this.groupRepository = groupRepository;
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public Exercise updateExercise(Long groupId, Long exerciseId, ExerciseUpdateDTO exerciseUpdateDTO) {
        User user = this.userService.obterUsuarioNaSessao();
        Group group = this.groupRepository.findByIdAndAdminId(groupId,user.getProfile().getId()).orElseThrow(GroupNotFoundException::new);

        ExerciseUtil.checkPermissionExercise(user,group);

        Exercise exercise = this.exerciseRepository.findByIdAndGroupId(exerciseId, groupId).orElseThrow(ExerciseNotFoundException::new);
        if (exercise.isInactivate()){
            throw new UnauthorizedException();
        }
        exercise.setTitle(exerciseUpdateDTO.getTitle());

        return this.exerciseRepository.save(exercise);
    }
}
