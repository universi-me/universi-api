package me.universi.question.services;

import jakarta.transaction.Transactional;
import me.universi.exercise.ExerciseRepository;
import me.universi.exercise.entities.Exercise;
import me.universi.exercise.services.ExerciseGetService;
import me.universi.group.entities.Group;
import me.universi.group.repositories.GroupRepository;
import me.universi.group.services.GroupService;
import me.universi.profile.entities.Profile;
import me.universi.question.QuestionRepository;
import me.universi.question.dto.QuestionCreateDTO;
import me.universi.question.entities.Question;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import me.universi.util.ExerciseUtil;

import java.util.UUID;


@Service
public class QuestionCreateServiceImpl implements QuestionCreateService {
    private final QuestionRepository questionRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseGetService exerciseGetService;
    private final GroupService groupService;
    private final UserService userService;

    private final GroupRepository groupRepository;

    @Autowired
    public QuestionCreateServiceImpl(QuestionRepository questionRepository, ExerciseRepository exerciseRepository, ExerciseGetService exerciseGetService, GroupService groupService, UserService userService, GroupRepository groupRepository) {
        this.questionRepository = questionRepository;
        this.exerciseRepository = exerciseRepository;
        this.exerciseGetService = exerciseGetService;
        this.groupService = groupService;
        this.userService = userService;
        this.groupRepository = groupRepository;
    }

    @Transactional
    public Question createQuestion(UUID groupId, UUID exerciseId, QuestionCreateDTO questionCreateDTO){

        Profile user = this.userService.getUserInSession().getProfile();
        Group group = this.groupService.findFirstById(groupId);

        ExerciseUtil.checkPermissionExercise(this.userService.getUserInSession(), group);

        Exercise exercise = this.exerciseGetService.getExercise(exerciseId, group.getId());

        questionCreateDTO.setProfileCreate(user);

        Question question = questionRepository.save(Question.from(questionCreateDTO));
        exercise.getQuestions().add(question);
        this.exerciseRepository.save(exercise);
        return question;
    }
}
