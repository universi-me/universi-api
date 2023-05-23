package me.universi.question.services;

import me.universi.group.entities.Group;
import me.universi.group.exceptions.GroupNotFoundException;
import me.universi.group.repositories.GroupRepository;
import me.universi.question.QuestionRepository;
import me.universi.question.entities.Question;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import me.universi.util.ExerciseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ListQuestionServiceImpl implements ListQuestionService {
    private final QuestionRepository questionRepository;
    private final UserService userService;
    private final GroupRepository groupRepository;

    @Autowired
    public ListQuestionServiceImpl(QuestionRepository questionRepository, UserService userService, GroupRepository groupRepository) {
        this.questionRepository = questionRepository;
        this.userService = userService;
        this.groupRepository = groupRepository;
    }

    @Override
    public List<Question> listQuestion(Long groupId, Long exerciseId) {
        User user = this.userService.obterUsuarioNaSessao();
        Group group = this.groupRepository.findByIdAndAdminId(groupId,user.getProfile().getId()).orElseThrow(GroupNotFoundException::new);

        ExerciseUtil.checkPermissionExercise(user,group);

        return  this.questionRepository.findAllByExercisesIdAndExercisesInactivateIsFalse(exerciseId);
    }

}
