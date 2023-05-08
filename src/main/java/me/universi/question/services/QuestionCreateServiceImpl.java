package me.universi.question.services;

import jakarta.transaction.Transactional;
import me.universi.exercise.ExerciseRepository;
import me.universi.exercise.entities.Exercise;
import me.universi.exercise.services.ExerciseGetService;
import me.universi.exercise.services.ListQuestionsWithAlternativesService;
import me.universi.feedback.FeedbackRepository;
import me.universi.feedback.entities.Feedback;
import me.universi.group.entities.Group;
import me.universi.group.repositories.GroupRepository;
import me.universi.group.services.GroupService;
import me.universi.question.QuestionRepository;
import me.universi.question.dto.QuestionCreateDTO;
import me.universi.question.entities.Question;
import me.universi.user.entities.User;
import me.universi.user.exceptions.UnauthorizedException;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.ExerciseUtil;


@Service
public class QuestionCreateServiceImpl implements QuestionCreateService {
    private final QuestionRepository questionRepository;
    private final FeedbackRepository feedbackRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseGetService exerciseGetService;
    private final GroupService groupService;
    private final UserService userService;

    private final GroupRepository groupRepository;

    @Autowired
    public QuestionCreateServiceImpl(QuestionRepository questionRepository, FeedbackRepository feedbackRepository, ListQuestionsWithAlternativesService listQuestionsWithAlternativesService, ExerciseRepository exerciseRepository, ExerciseGetService exerciseGetService, GroupService groupService, UserService userService, GroupRepository groupRepository) {
        this.questionRepository = questionRepository;
        this.feedbackRepository = feedbackRepository;
        this.exerciseRepository = exerciseRepository;
        this.exerciseGetService = exerciseGetService;
        this.groupService = groupService;
        this.userService = userService;
        this.groupRepository = groupRepository;
    }

    @Transactional
    public Question createQuestion(Long groupId, Long exerciseId, QuestionCreateDTO questionCreateDTO){

        User user = this.userService.obterUsuarioNaSessao();
        Group group = this.groupService.findFirstById(groupId);

        ExerciseUtil.checkPermissionExercise(this.userService.obterUsuarioNaSessao(), group);

        Exercise exercise = this.exerciseGetService.getExercise(exerciseId, group.getId());
        Feedback feedback = feedbackRepository.save(questionCreateDTO.getFeedback());

        questionCreateDTO.setUserCreate(user);
        questionCreateDTO.setFeedback(feedback);

        Question question = questionRepository.save(Question.from(questionCreateDTO));
        exercise.getQuestions().add(question);
        this.exerciseRepository.save(exercise);
        return question;
    }
}
