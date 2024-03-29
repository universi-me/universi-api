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

import java.util.UUID;

@Service
public class DeleteExerciseServiceImpl implements DeleteExerciseService{

    private final UserService userService;
    private final GroupRepository groupRepository;
    private final ExerciseRepository exerciseRepository;
    private static final int MIN_AMOUNT_QUESTIONS_DELETE = 5;


    @Autowired
    public DeleteExerciseServiceImpl(UserService userService, GroupRepository groupRepository, ExerciseRepository exerciseRepository) {
        this.userService = userService;
        this.groupRepository = groupRepository;
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public void deleteExercise(UUID groupId, UUID exerciseId) {
        User user = this.userService.getUserInSession();
        Group group = this.groupRepository.findFirstByIdAndAdminId(groupId, user.getProfile().getId()).orElseThrow(GroupNotFoundException::new);

        ExerciseUtil.checkPermissionExercise(user, group);

        Exercise exercise = this.exerciseRepository.findFirstByIdAndGroupId(exerciseId, groupId).orElseThrow(ExerciseNotFoundException::new);

        if (exercise.getQuestions() != null && exercise.getQuestions().size() <= MIN_AMOUNT_QUESTIONS_DELETE){
            this.exerciseRepository.delete(exercise);
        }else {
            exercise.setInactivate(true);
            this.exerciseRepository.save(exercise);
        }
    }
}
