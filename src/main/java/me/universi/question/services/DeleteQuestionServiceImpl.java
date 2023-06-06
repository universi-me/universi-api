package me.universi.question.services;

import me.universi.group.entities.Group;
import me.universi.group.exceptions.GroupNotFoundException;
import me.universi.group.repositories.GroupRepository;
import me.universi.question.QuestionRepository;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import me.universi.util.ExerciseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DeleteQuestionServiceImpl implements DeleteQuestionService {
    private final QuestionRepository questionRepository;
    private final UserService userService;
    private final GroupRepository groupRepository;

    @Autowired
    public DeleteQuestionServiceImpl(QuestionRepository questionRepository, UserService userService, GroupRepository groupRepository) {
        this.questionRepository = questionRepository;
        this.userService = userService;
        this.groupRepository = groupRepository;
    }

    @Override
    public void deleteQuestion(Long groupId, Long exerciseId, Long questionId) {
        User user = this.userService.getUserInSession();
        Group group = this.groupRepository.findByIdAndAdminId(groupId, user.getProfile().getId()).orElseThrow(GroupNotFoundException::new);

        ExerciseUtil.checkPermissionExercise(user, group);

        questionRepository.deleteById(questionId);
    }
}
