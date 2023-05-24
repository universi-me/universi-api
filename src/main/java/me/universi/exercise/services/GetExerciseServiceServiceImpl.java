package me.universi.exercise.services;

import me.universi.exercise.ExerciseRepository;
import me.universi.exercise.entities.Exercise;
import me.universi.exercise.exception.ExerciseNotFoundException;
import me.universi.group.entities.Group;
import me.universi.group.exceptions.GroupNotFoundException;
import me.universi.group.repositories.GroupRepository;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import me.universi.util.ExerciseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetExerciseServiceServiceImpl implements GetExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final UserService userService;
    private final GroupRepository groupRepository;

    @Autowired
    public GetExerciseServiceServiceImpl(ExerciseRepository exerciseRepository, UserService userService, GroupRepository groupRepository) {
        this.exerciseRepository = exerciseRepository;
        this.userService = userService;
        this.groupRepository = groupRepository;
    }

    public Exercise getExercise(Long groupId, Long exerciseId) {
        User user = this.userService.getUserInSession();
        Group group = this.groupRepository.findByIdAndAdminId(groupId, user.getProfile().getId())
                .orElseThrow(GroupNotFoundException::new);

        ExerciseUtil.checkPermissionExercise(user, group);
        return this.exerciseRepository.findByIdAndGroupId(exerciseId, groupId)
                .orElseThrow(ExerciseNotFoundException::new);

    }
}
