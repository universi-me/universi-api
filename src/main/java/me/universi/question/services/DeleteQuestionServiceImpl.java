package me.universi.question.services;

import java.util.Optional;
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

import java.util.UUID;


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
    public void deleteQuestion(UUID groupId, UUID exerciseId, UUID questionId) {
        User user = this.userService.getUserInSession();
        Group group = this.groupRepository.findFirstByIdAndAdminId(groupId, user.getProfile().getId()).orElseThrow(GroupNotFoundException::new);

        ExerciseUtil.checkPermissionExercise(user, group);

        Optional<Question> question = questionRepository.findById(questionId);
        if(question.isPresent()) {
            question.get().setDeleted(true);
            questionRepository.save(question.get());
        }
    }
}
